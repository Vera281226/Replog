package pack.model.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import pack.model.theater.PartyPost;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Integer chatRoomId;
    
    @Column(name = "room_name", length = 100)
    private String roomName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false)
    private RoomType roomType;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatParticipant> participants;
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages;
    
    @ManyToOne
    @JoinColumn(name = "party_post_no")
    private PartyPost partyPost;
    
    public enum RoomType {
        PARTY, AI
    }
}