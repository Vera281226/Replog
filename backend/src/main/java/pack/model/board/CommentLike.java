package pack.model.board;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comment_likes")
@IdClass(CommentLikeId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {

    @Id
    @Column(name = "member_id", nullable = false, length = 20)
    private String memberId;

    @Id
    @Column(name = "comment_no", nullable = false)
    private Integer commentNo;

    @Column(name = "liked_at")
    private LocalDateTime likedAt;

    @PrePersist
    protected void onCreate() {
        if (likedAt == null) {
            this.likedAt = LocalDateTime.now();
        }
    }
}