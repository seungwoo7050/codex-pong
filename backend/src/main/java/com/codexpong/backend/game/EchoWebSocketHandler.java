package com.codexpong.backend.game;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * [핸들러] backend/src/main/java/com/codexpong/backend/game/EchoWebSocketHandler.java
 * 설명:
 *   - 프런트엔드가 WebSocket 연결을 확인할 수 있도록 단순 에코 메시지를 반환한다.
 *   - 추후 게임 이벤트 전송 로직을 추가하기 위한 기본 골격이다.
 * 버전: v0.1.0
 * 관련 설계문서:
 *   - design/realtime/v0.1.0-basic-websocket-wiring.md
 * 변경 이력:
 *   - v0.1.0: 에코 핸들러 최초 구현
 */
@Component
public class EchoWebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        session.sendMessage(new TextMessage("echo: " + message.getPayload()));
    }
}
