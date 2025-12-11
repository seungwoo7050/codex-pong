package com.codexpong.backend.config;

import com.codexpong.backend.game.EchoWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * [설정] backend/src/main/java/com/codexpong/backend/config/WebSocketConfig.java
 * 설명:
 *   - WebSocket 핸들러 등록을 통해 기본 에코 엔드포인트를 노출한다.
 *   - v0.1.0에서 프런트엔드와의 연결 테스트를 위한 최소 설정만 포함한다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/realtime/v0.1.0-basic-websocket-wiring.md
 * 변경 이력:
 *   - v0.1.0: 에코 핸들러 등록 추가
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final EchoWebSocketHandler echoWebSocketHandler;

    public WebSocketConfig(EchoWebSocketHandler echoWebSocketHandler) {
        this.echoWebSocketHandler = echoWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoWebSocketHandler, "/ws/echo").setAllowedOrigins("*");
    }
}
