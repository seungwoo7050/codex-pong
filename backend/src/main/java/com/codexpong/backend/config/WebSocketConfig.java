package com.codexpong.backend.config;

import com.codexpong.backend.chat.ChatWebSocketHandler;
import com.codexpong.backend.game.EchoWebSocketHandler;
import com.codexpong.backend.game.GameWebSocketHandler;
import com.codexpong.backend.social.SocialWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * [설정] backend/src/main/java/com/codexpong/backend/config/WebSocketConfig.java
 * 설명:
 *   - WebSocket 핸들러 등록을 통해 기본 에코 엔드포인트를 노출한다.
 *   - JWT 기반 핸드셰이크를 통해 인증 사용자 정보를 세션에 연결한다.
 *   - v0.3.0에서 게임 방 핸들러를, v0.5.0에서 친구 알림 핸들러를, v0.6.0에서 채팅 핸들러를 추가한다.
 * 버전: v0.6.0
 * 관련 설계문서:
 *   - design/realtime/v0.1.0-basic-websocket-wiring.md
 *   - design/backend/v0.2.0-auth-and-profile.md
 *   - design/backend/v0.4.0-ranking-system.md
 *   - design/backend/v0.5.0-friends-and-blocks.md
 *   - design/backend/v0.6.0-chat-and-channels.md
 * 변경 이력:
 *   - v0.1.0: 에코 핸들러 등록 추가
 *   - v0.2.0: JWT 인증 인터셉터와 핸드셰이크 핸들러 연결
 *   - v0.3.0: 게임 방 핸들러 추가
 *   - v0.5.0: 친구 알림 핸들러 추가
 *   - v0.6.0: 채팅 핸들러 추가
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final EchoWebSocketHandler echoWebSocketHandler;
    private final GameWebSocketHandler gameWebSocketHandler;
    private final SocialWebSocketHandler socialWebSocketHandler;
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final WebSocketAuthHandshakeInterceptor webSocketAuthHandshakeInterceptor;

    public WebSocketConfig(EchoWebSocketHandler echoWebSocketHandler,
            GameWebSocketHandler gameWebSocketHandler,
            SocialWebSocketHandler socialWebSocketHandler,
            ChatWebSocketHandler chatWebSocketHandler,
            WebSocketAuthHandshakeInterceptor webSocketAuthHandshakeInterceptor) {
        this.echoWebSocketHandler = echoWebSocketHandler;
        this.gameWebSocketHandler = gameWebSocketHandler;
        this.socialWebSocketHandler = socialWebSocketHandler;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.webSocketAuthHandshakeInterceptor = webSocketAuthHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoWebSocketHandler, "/ws/echo")
                .addInterceptors(webSocketAuthHandshakeInterceptor)
                .setHandshakeHandler(new WebSocketUserHandshakeHandler())
                .setAllowedOrigins("*");

        registry.addHandler(gameWebSocketHandler, "/ws/game")
                .addInterceptors(webSocketAuthHandshakeInterceptor)
                .setHandshakeHandler(new WebSocketUserHandshakeHandler())
                .setAllowedOrigins("*");

        registry.addHandler(socialWebSocketHandler, "/ws/social")
                .addInterceptors(webSocketAuthHandshakeInterceptor)
                .setHandshakeHandler(new WebSocketUserHandshakeHandler())
                .setAllowedOrigins("*");

        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .addInterceptors(webSocketAuthHandshakeInterceptor)
                .setHandshakeHandler(new WebSocketUserHandshakeHandler())
                .setAllowedOrigins("*");
    }
}
