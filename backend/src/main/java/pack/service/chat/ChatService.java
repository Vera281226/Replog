// ChatService.java
package pack.service.chat;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import pack.dto.chat.ChatRoomRequest;
import pack.dto.chat.ChatRoomResponse;
import pack.request.chat.ChatMessageRequest;
import pack.response.chat.ChatMessageResponse;

public interface ChatService {
	List<ChatRoomResponse> getChatRooms(String memberId);
	ChatRoomResponse createChatRoom(ChatRoomRequest request, String creatorId);

	List<ChatRoomResponse> getMyChatRooms(String memberId);

	void joinChatRoom(Integer roomId, String memberId);

	ChatMessageResponse sendMessage(ChatMessageRequest request, String senderId);

	Page<ChatMessageResponse> getChatMessages(Integer roomId, String memberId, Pageable pageable);

	ChatRoomResponse getOrCreateAiChatRoom(String memberId);

	ChatMessageResponse sendAiMessage(String userMessage, String memberId);

	ChatRoomResponse getChatRoom(Integer roomId);

	List<ChatRoomResponse> getAllActiveRooms();

	void leaveChatRoom(Integer roomId, String memberId);

	void deleteChatRoom(Integer roomId, String memberId);

}