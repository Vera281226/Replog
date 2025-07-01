// ChatRoomResponse.java
package pack.response.chat;

import lombok.Builder;
import lombok.Getter;
import pack.model.chat.ChatRoom;
import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomResponse {
    private Integer chatRoomId;
    private Integer partyPostNo;
    private String roomName;
    private ChatRoom.RoomType roomType;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private Long participantCount;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
