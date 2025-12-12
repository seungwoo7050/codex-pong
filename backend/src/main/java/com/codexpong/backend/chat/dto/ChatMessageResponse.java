package com.codexpong.backend.chat.dto;

import com.codexpong.backend.chat.domain.ChatMessage;
import java.time.LocalDateTime;

/**
 * [응답 DTO] backend/src/main/java/com/codexpong/backend/chat/dto/ChatMessageResponse.java
 * 설명:
 *   - 채널별 메시지를 클라이언트로 전달하기 위한 표현 객체다.
 *   - WebSocket과 REST 응답에서 동일하게 사용한다.
 * 버전: v0.6.0
 * 관련 설계문서:
 *   - design/backend/v0.6.0-chat-and-channels.md
 */
public class ChatMessageResponse {

    private Long id;
    private String channelType;
    private String channelKey;
    private Long senderId;
    private String senderNickname;
    private Long recipientId;
    private String content;
    private LocalDateTime createdAt;

    public ChatMessageResponse(ChatMessage message) {
        this.id = message.getId();
        this.channelType = message.getChannelType().name();
        this.channelKey = message.getChannelKey();
        this.senderId = message.getSender().getId();
        this.senderNickname = message.getSender().getNickname();
        this.recipientId = message.getRecipient() != null ? message.getRecipient().getId() : null;
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getChannelType() {
        return channelType;
    }

    public String getChannelKey() {
        return channelKey;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
