package com.codexpong.backend.game;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [컨트롤러] backend/src/main/java/com/codexpong/backend/game/GameResultController.java
 * 설명:
 *   - 테스트용 경기 생성 및 결과 조회 API를 제공한다.
 *   - v0.1.0에서는 인증 없이 단순 흐름만 제공하여 전체 스택 연동을 검증한다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/backend/v0.1.0-core-skeleton-and-health.md
 * 변경 이력:
 *   - v0.1.0: 경기 생성/조회 API 추가
 */
@RestController
@RequestMapping("/api/games")
public class GameResultController {

    private final GameResultService gameResultService;

    public GameResultController(GameResultService gameResultService) {
        this.gameResultService = gameResultService;
    }

    /**
     * 설명:
     *   - 간단한 테스트 경기를 생성하고 결과를 저장한다.
     * 입력:
     *   - GameResultRequest: 참여자 이름과 점수.
     * 출력:
     *   - 생성된 엔티티 정보를 응답 DTO로 반환한다.
     */
    @PostMapping
    public ResponseEntity<GameResultResponse> createGame(@Valid @RequestBody GameResultRequest request) {
        GameResult saved = gameResultService.saveResult(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(GameResultResponse.from(saved));
    }

    /**
     * 설명:
     *   - 저장된 테스트 경기 목록을 조회한다.
     * 출력:
     *   - GameResultResponse 리스트.
     */
    @GetMapping
    public List<GameResultResponse> listGames() {
        return gameResultService.findRecentResults().stream()
                .map(GameResultResponse::from)
                .collect(Collectors.toList());
    }
}
