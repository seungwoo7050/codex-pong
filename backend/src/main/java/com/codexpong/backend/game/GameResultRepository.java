package com.codexpong.backend.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * [저장소] backend/src/main/java/com/codexpong/backend/game/GameResultRepository.java
 * 설명:
 *   - 테스트 경기 결과를 조회/저장하기 위한 JPA 리포지토리 인터페이스다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/backend/v0.1.0-core-skeleton-and-health.md
 * 변경 이력:
 *   - v0.1.0: 기본 CRUD 지원 인터페이스 정의
 */
@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {
}
