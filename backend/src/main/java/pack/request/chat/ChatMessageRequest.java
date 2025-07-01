package pack.request.chat;

import lombok.Getter;
import lombok.Setter;

//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class ChatMessageRequest {
    
//    @NotNull(message = "채팅방 ID는 필수입니다")
    private Integer chatRoomId;
    
//    @NotBlank(message = "메시지 내용은 필수입니다")
    private String messageText;
}
