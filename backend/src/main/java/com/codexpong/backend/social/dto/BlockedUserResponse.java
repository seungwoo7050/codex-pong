package com.codexpong.backend.social.dto;

import com.codexpong.backend.social.domain.Block;
import java.time.LocalDateTime;

/**
 * [응답 DTO] backend/src/main/java/com/codexpong/backend/social/dto/BlockedUserResponse.java
 * 설명:
 *   - 차단한 사용자 정보를 닉네임과 함께 반환한다.
 * 버전: v0.5.0
 * 관련 설계문서:
 *   - design/backend/v0.5.0-friends-and-blocks.md
 */
public record BlockedUserResponse(Long userId, String nickname, LocalDateTime blockedAt) {

    public static BlockedUserResponse from(Block block) {
        return new BlockedUserResponse(block.getBlocked().getId(), block.getBlocked().getNickname(),
                block.getCreatedAt());
    }
}
