// src/main/java/pack/model/chat/ChatParticipant.java
package pack.model.chat;

import jakarta.persistence.*;
import lombok.*;
import pack.model.member.Member;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ChatParticipantId.class)
public class ChatParticipant {
    
    @Id
    @Column(name = "chat_room_id")
    private Integer chatRoomId;
    
    @Id
    @Column(name = "member_id", length = 20)
    private String memberId;
    
    @Column(name = "joined_at")
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();
    
    @Column(name = "last_read_message_id")
    private Integer lastReadMessageId;
    
    // ✅ JPA 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", insertable = false, updatable = false)
    private ChatRoom chatRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_read_message_id", insertable = false, updatable = false)
    private ChatMessage lastReadMessage;
}
