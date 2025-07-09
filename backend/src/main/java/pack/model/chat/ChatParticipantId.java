package pack.model.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantId implements Serializable {
    private static final long serialVersionUID = 1L;
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
    
    @Override
    public String toString() {
        return "ChatParticipantId{" +
                "chatRoomId=" + chatRoomId +
                ", memberId='" + memberId + '\'' +
                '}';
    }
}
