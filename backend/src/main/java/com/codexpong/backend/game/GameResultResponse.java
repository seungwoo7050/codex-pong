package com.codexpong.backend.game;

import java.time.LocalDateTime;

/**
 * [DTO] backend/src/main/java/com/codexpong/backend/game/GameResultResponse.java
 * 설명:
 *   - 클라이언트에 노출할 경기 결과 정보를 단순화한 응답 모델이다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/backend/v0.1.0-core-skeleton-and-health.md
 * 변경 이력:
 *   - v0.1.0: 엔티티 매핑 전용 DTO 추가
 */
public record GameResultResponse(
        Long id,
        String playerA,
        String playerB,
        int scoreA,
        int scoreB,
        LocalDateTime playedAt
) {

    public static GameResultResponse from(GameResult entity) {
        return new GameResultResponse(
                entity.getId(),
                entity.getPlayerA(),
                entity.getPlayerB(),
                entity.getScoreA(),
                entity.getScoreB(),
                entity.getPlayedAt()
        );
    }
}
