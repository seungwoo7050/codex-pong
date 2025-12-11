package com.codexpong.backend.game;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * [엔티티] backend/src/main/java/com/codexpong/backend/game/GameResult.java
 * 설명:
 *   - 테스트용 경기 결과를 저장하기 위한 단순 엔티티다.
 *   - v0.1.0에서는 스코어 및 참여자 이름만 보관한다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/backend/v0.1.0-core-skeleton-and-health.md
 * 변경 이력:
 *   - v0.1.0: 기본 필드 정의 및 자동 증가 ID 추가
 */
@Entity
@Table(name = "game_results")
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String playerA;

    @Column(nullable = false, length = 50)
    private String playerB;

    @Column(nullable = false)
    private int scoreA;

    @Column(nullable = false)
    private int scoreB;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    protected GameResult() {
    }

    public GameResult(String playerA, String playerB, int scoreA, int scoreB, LocalDateTime playedAt) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.playedAt = playedAt;
    }

    public Long getId() {
        return id;
    }

    public String getPlayerA() {
        return playerA;
    }

    public String getPlayerB() {
        return playerB;
    }

    public int getScoreA() {
        return scoreA;
    }

    public int getScoreB() {
        return scoreB;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }
}
