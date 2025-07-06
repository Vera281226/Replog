package pack.service.chat;

import java.util.List;

import pack.dto.chat.ChatRoomRequest;
import pack.dto.chat.ChatRoomResponse;

public interface ChatRoomService {
    ChatRoomResponse createChatRoom(ChatRoomRequest request, String creatorId);
    List<ChatRoomResponse> getMyChatRooms(String memberId);
    boolean joinChatRoom(Integer roomId, String memberId);
    void leaveChatRoom(Integer roomId, String memberId);
    void deleteChatRoom(Integer roomId, String memberId);
    ChatRoomResponse getChatRoom(Integer roomId);
    List<ChatRoomResponse> getAllActiveRooms();
	ChatRoomResponse getRoomByPartyPostNo(Integer partyPostNo);
}
