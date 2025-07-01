package pack.request.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pack.model.chat.ChatRoom;

@Getter
@Setter
@Builder
public class ChatRoomRequest {
    private Integer partyPostNo;
    private String roomName;
    private ChatRoom.RoomType roomType;
}