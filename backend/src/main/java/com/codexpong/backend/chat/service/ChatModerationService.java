package com.codexpong.backend.chat.service;

import com.codexpong.backend.chat.domain.ChatMute;
import com.codexpong.backend.chat.repository.ChatMuteRepository;
import com.codexpong.backend.user.domain.User;
import com.codexpong.backend.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * [서비스] backend/src/main/java/com/codexpong/backend/chat/service/ChatModerationService.java
 * 설명:
 *   - 간단한 뮤트 조회/등록 훅을 제공해 채팅 도메인 제재를 수행한다.
 *   - 현재는 내부용 API로 사용되며, 이후 관리자 UI와 연동될 수 있도록 확장 지점을 남긴다.
 * 버전: v0.6.0
 * 관련 설계문서:
 *   - design/backend/v0.6.0-chat-and-channels.md
 */
@Service
@Transactional
public class ChatModerationService {

    private final ChatMuteRepository chatMuteRepository;
    private final UserRepository userRepository;

    public ChatModerationService(ChatMuteRepository chatMuteRepository, UserRepository userRepository) {
        this.chatMuteRepository = chatMuteRepository;
        this.userRepository = userRepository;
    }

    public boolean isMuted(Long userId) {
        return chatMuteRepository.findActiveMute(userId, LocalDateTime.now()).isPresent();
    }

    public ChatMute muteUser(Long targetUserId, String reason, LocalDateTime expiresAt) {
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "대상 사용자를 찾을 수 없습니다."));
        return chatMuteRepository.save(new ChatMute(target, reason, expiresAt));
    }

    public void cleanupExpiredMutes() {
        LocalDateTime now = LocalDateTime.now();
        chatMuteRepository.findAll().stream()
                .filter(mute -> mute.isExpired(now))
                .forEach(chatMuteRepository::delete);
    }
}
