package pack.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pack.model.chat.ChatRoom.RoomType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    
    private Integer chatRoomId;
    private Integer partyPostNo;
    private String roomName;
    private RoomType roomType;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private Long participantCount;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
