package com.codexpong.backend.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codexpong.backend.game.GameResult;
import com.codexpong.backend.game.GameResultRepository;
import com.codexpong.backend.game.domain.GameRoom;
import com.codexpong.backend.game.domain.MatchType;
import com.codexpong.backend.game.engine.model.GameSnapshot;
import com.codexpong.backend.replay.Replay;
import com.codexpong.backend.replay.ReplayRepository;
import com.codexpong.backend.replay.ReplayService;
import com.codexpong.backend.user.domain.User;
import com.codexpong.backend.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * [통합 테스트] backend/src/test/java/com/codexpong/backend/job/JobFlowTest.java
 * 설명:
 *   - v0.12.0 잡 생성 → Redis 진행률/완료 메시지 → WebSocket 알림 → 결과 다운로드까지 연계 흐름을 검증한다.
 *   - Redis Streams 컨슈머 그룹과 WebSocket 브로드캐스트가 정상 작동하는지 확인한다.
 * 버전: v0.12.0
 * 관련 설계문서:
 *   - design/backend/v0.12.0-jobs-api-and-state-machine.md
 *   - design/realtime/v0.12.0-job-progress-events.md
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class JobFlowTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        try {
            if (!redis.isRunning()) {
                redis.start();
            }
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "Docker 환경이 필요합니다: " + ex.getMessage());
        }
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("jobs.queue.enabled", () -> true);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReplayService replayService;

    @Autowired
    private ReplayRepository replayRepository;

    @Autowired
    private GameResultRepository gameResultRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Autowired
    private JobQueueProperties jobQueueProperties;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JobRepository jobRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void clean() {
        jobRepository.deleteAll();
        replayRepository.deleteAll();
        gameResultRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void 잡_생성부터_WebSocket_알림과_상태변경까지_완료된다() throws Exception {
        String ownerToken = obtainToken("job-owner");
        obtainToken("job-opponent");
        User owner = userRepository.findByUsername("job-owner").orElseThrow();
        User opponent = userRepository.findByUsername("job-opponent").orElseThrow();

        Replay replay = createReplay(owner, opponent);

        var createResponse = mockMvc.perform(post("/api/replays/" + replay.getId() + "/exports/mp4")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> body = objectMapper.readValue(createResponse.getResponse().getContentAsString(), Map.class);
        Long jobId = Long.valueOf(body.get("jobId").toString());

        CompletableFuture<String> wsPayload = new CompletableFuture<>();
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketSession session = client.doHandshake(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                if (message.getPayload().contains("job.progress")) {
                    wsPayload.complete(message.getPayload());
                }
            }
        }, null, URI.create(String.format("ws://localhost:%d/ws/jobs?token=%s", port, ownerToken))).get(5, TimeUnit.SECONDS);

        redisTemplate.opsForStream().add(jobQueueProperties.getProgressStream(), Map.of(
                "jobId", jobId.toString(),
                "progress", "40",
                "phase", "ENCODE",
                "message", "인코딩 중"
        ));

        String progressPayload = wsPayload.get(5, TimeUnit.SECONDS);
        assertThat(progressPayload).contains("job.progress").contains(jobId.toString());

        Path resultPath = Path.of("build/test-exports/job-" + jobId + ".mp4");
        Files.createDirectories(resultPath.getParent());
        Files.writeString(resultPath, "dummy");
        redisTemplate.opsForStream().add(jobQueueProperties.getResultStream(), Map.of(
                "jobId", jobId.toString(),
                "status", "SUCCEEDED",
                "resultUri", resultPath.toString(),
                "checksum", "abc"
        ));

        waitForStatus(jobId, JobStatus.SUCCEEDED);
        ResponseEntity<String> jobResponse = restTemplate.getForEntity("http://localhost:" + port + "/api/jobs/" + jobId,
                String.class);
        assertThat(jobResponse.getBody()).contains("SUCCEEDED");

        ResponseEntity<byte[]> downloadResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/jobs/" + jobId + "/result", byte[].class);
        assertThat(downloadResponse.getStatusCode().is2xxSuccessful()).isTrue();

        session.close();
    }

    private String obtainToken(String username) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", "password123",
                                "nickname", "잡테스터",
                                "avatarUrl", ""
                        ))))
                .andExpect(status().isOk());

        var login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", username,
                                "password", "password123"
                        ))))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, Object> response = objectMapper.readValue(login.getResponse().getContentAsString(), Map.class);
        return response.get("token").toString();
    }

    private Replay createReplay(User owner, User opponent) {
        GameRoom room = new GameRoom(owner, opponent, MatchType.NORMAL);
        replayService.startRecording(room);
        replayService.appendSnapshot(room.getRoomId(), room.currentSnapshot());
        replayService.appendSnapshot(room.getRoomId(), new GameSnapshot(room.getRoomId(), 0, 0, 0, 0, 10, 20, 5, 3, 5, true));
        GameResult result = gameResultRepository.save(new GameResult(owner, opponent, 5, 3, room.getRoomId(),
                MatchType.NORMAL, 0, 0, owner.getRating(), opponent.getRating(),
                LocalDateTime.now(), LocalDateTime.now()));
        return replayService.completeRecording(room, result).get(0);
    }

    private void waitForStatus(Long jobId, JobStatus status) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Job job = jobRepository.findById(jobId).orElseThrow();
            if (job.getStatus() == status) {
                return;
            }
            Thread.sleep(300);
        }
        Job job = jobRepository.findById(jobId).orElseThrow();
        assertThat(job.getStatus()).isEqualTo(status);
    }
}
