package pack.model.board;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PostLikeId.class)
public class PostLike {

    @Id
    @Column(length = 20)
    private String id; // member.id

    @Id
    private Integer postNo; // posts.post_no

    private LocalDateTime likedAt;

    @PrePersist
    protected void onCreate() {
        this.likedAt = LocalDateTime.now();
    }
}