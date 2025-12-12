package com.codexpong.backend.social.dto;

import com.codexpong.backend.social.domain.FriendRequest;
import com.codexpong.backend.social.domain.FriendRequestStatus;
import java.time.LocalDateTime;

/**
 * [응답 DTO] backend/src/main/java/com/codexpong/backend/social/dto/FriendRequestResponse.java
 * 설명:
 *   - 친구 요청의 상태와 송신/수신 사용자 정보를 반환한다.
 * 버전: v0.5.0
 * 관련 설계문서:
 *   - design/backend/v0.5.0-friends-and-blocks.md
 */
public record FriendRequestResponse(Long id, Long senderId, String senderNickname, Long receiverId,
        FriendRequestStatus status, LocalDateTime createdAt, LocalDateTime respondedAt) {

    public static FriendRequestResponse from(FriendRequest request) {
        return new FriendRequestResponse(
                request.getId(),
                request.getSender().getId(),
                request.getSender().getNickname(),
                request.getReceiver().getId(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getRespondedAt()
        );
    }
}
