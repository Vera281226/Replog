package pack.dto.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import pack.model.chat.ChatRoom.RoomType;

@Getter
@Setter
@Builder
public class ChatRoomRequest {
    
    private Integer partyPostNo;
    
    private String roomName;
    private RoomType roomType;
}