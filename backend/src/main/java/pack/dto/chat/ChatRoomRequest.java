package pack.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pack.model.chat.ChatRoom.RoomType;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class ChatRoomRequest {
    
    private Integer partyPostNo;
    
//    @NotBlank(message = "채팅방 이름은 필수입니다")
    private String roomName;
    
//    @NotNull(message = "채팅방 타입은 필수입니다")
    private RoomType roomType;
}