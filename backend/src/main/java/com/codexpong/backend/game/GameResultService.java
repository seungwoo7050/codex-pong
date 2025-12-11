package com.codexpong.backend.game;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [서비스] backend/src/main/java/com/codexpong/backend/game/GameResultService.java
 * 설명:
 *   - 테스트 경기 결과 생성 및 조회 흐름을 관리한다.
 *   - v0.1.0에서는 단순히 입력값을 저장하고 최근 기록을 반환한다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/backend/v0.1.0-core-skeleton-and-health.md
 * 변경 이력:
 *   - v0.1.0: 서비스 계층 최초 구현
 */
@Service
public class GameResultService {

    private final GameResultRepository gameResultRepository;

    public GameResultService(GameResultRepository gameResultRepository) {
        this.gameResultRepository = gameResultRepository;
    }

    @Transactional
    public GameResult saveResult(GameResultRequest request) {
        GameResult gameResult = new GameResult(
                request.playerA(),
                request.playerB(),
                request.scoreA(),
                request.scoreB(),
                LocalDateTime.now()
        );
        return gameResultRepository.save(gameResult);
    }

    @Transactional(readOnly = true)
    public List<GameResult> findRecentResults() {
        return gameResultRepository.findAll();
    }
}
