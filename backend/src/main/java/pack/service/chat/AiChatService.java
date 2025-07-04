package pack.service.chat;

import pack.dto.chat.ChatMessageResponse;
import pack.dto.chat.ChatRoomResponse;

public interface AiChatService {
    ChatRoomResponse getOrCreateAiChatRoom(String memberId);
    ChatMessageResponse sendAiMessage(String userMessage, String memberId);
}
