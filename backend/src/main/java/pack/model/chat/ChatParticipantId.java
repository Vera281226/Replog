// src/main/java/pack/model/chat/ChatParticipantId.java
package pack.model.chat;

import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatParticipantId implements Serializable {
    private Integer chatRoomId;
    private String memberId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatParticipantId that = (ChatParticipantId) o;
        return Objects.equals(chatRoomId, that.chatRoomId) && 
               Objects.equals(memberId, that.memberId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(chatRoomId, memberId);
    }
}
