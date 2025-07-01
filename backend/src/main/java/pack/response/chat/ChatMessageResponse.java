package pack.response.chat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponse {
    
    private Integer chatMessagesId;
    private Integer chatRoomId;
    private String senderId;
    private String senderNickname;
    private String messageText;
    private LocalDateTime sentAt;
}