package com.codexpong.backend.game;

import com.codexpong.backend.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [서비스] backend/src/main/java/com/codexpong/backend/game/GameResultService.java
 * 설명:
 *   - 실시간 경기 종료 시 결과를 생성하고 최근 전적을 조회한다.
 *   - v0.3.0에서는 User 엔티티를 연결하고 방 정보를 기록한다.
 * 버전: v0.3.0
 * 관련 설계문서:
 *   - design/backend/v0.3.0-game-and-matchmaking.md
 * 변경 이력:
 *   - v0.1.0: 서비스 계층 최초 구현
 *   - v0.3.0: 사용자 연계 및 자동 기록 로직으로 확장
 */
@Service
public class GameResultService {

    private final GameResultRepository gameResultRepository;

    public GameResultService(GameResultRepository gameResultRepository) {
        this.gameResultRepository = gameResultRepository;
    }

    @Transactional
    public GameResult recordResult(String roomId, User playerA, User playerB, int scoreA, int scoreB,
            LocalDateTime startedAt, LocalDateTime finishedAt) {
        GameResult gameResult = new GameResult(playerA, playerB, scoreA, scoreB, roomId, startedAt, finishedAt);
        return gameResultRepository.save(gameResult);
    }

    @Transactional(readOnly = true)
    public List<GameResult> findRecentResults() {
        return gameResultRepository.findTop20ByOrderByFinishedAtDesc();
    }
}
