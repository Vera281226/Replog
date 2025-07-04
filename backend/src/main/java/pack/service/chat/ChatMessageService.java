package pack.service.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import pack.dto.chat.ChatMessageRequest;
import pack.dto.chat.ChatMessageResponse;

public interface ChatMessageService {
    ChatMessageResponse sendMessage(ChatMessageRequest request, String senderId);
    Page<ChatMessageResponse> getChatMessages(Integer roomId, String memberId, Pageable pageable);
}