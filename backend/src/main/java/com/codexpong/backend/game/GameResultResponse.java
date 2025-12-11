package com.codexpong.backend.game;

import java.time.LocalDateTime;

/**
 * [DTO] backend/src/main/java/com/codexpong/backend/game/GameResultResponse.java
 * 설명:
 *   - 클라이언트에 노출할 경기 결과 정보를 단순화한 응답 모델이다.
 * 버전: v0.3.0
 * 관련 설계문서:
 *   - design/backend/v0.3.0-game-and-matchmaking.md
 * 변경 이력:
 *   - v0.1.0: 엔티티 매핑 전용 DTO 추가
 *   - v0.3.0: 사용자/시간/룸 정보를 포함하도록 확장
 */
public record GameResultResponse(
        Long id,
        Long playerAId,
        String playerANickname,
        Long playerBId,
        String playerBNickname,
        int scoreA,
        int scoreB,
        String roomId,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) {

    public static GameResultResponse from(GameResult entity) {
        return new GameResultResponse(
                entity.getId(),
                entity.getPlayerA().getId(),
                entity.getPlayerA().getNickname(),
                entity.getPlayerB().getId(),
                entity.getPlayerB().getNickname(),
                entity.getScoreA(),
                entity.getScoreB(),
                entity.getRoomId(),
                entity.getStartedAt(),
                entity.getFinishedAt()
        );
    }
}
