package com.codexpong.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [부트스트랩] backend/src/main/java/com/codexpong/backend/CodexPongApplication.java
 * 설명:
 *   - Spring Boot 애플리케이션의 진입점이다.
 *   - v0.1.0 기준 핵심 스켈레톤과 헬스체크, 테스트 게임 API, WebSocket 설정을 구동한다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/backend/v0.1.0-core-skeleton-and-health.md
 *   - design/realtime/v0.1.0-basic-websocket-wiring.md
 * 변경 이력:
 *   - v0.1.0: 프로젝트 부트스트랩 생성
 */
@SpringBootApplication
public class CodexPongApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodexPongApplication.class, args);
    }
}
