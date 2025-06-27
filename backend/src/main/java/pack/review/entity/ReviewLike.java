package pack.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "review_likes")
@IdClass(ReviewLikeId.class)  // 복합키 클래스 명시
public class ReviewLike {
    @Id
    @Column(name = "member_id")
    private String memberId;

    @Id
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", insertable = false, updatable = false)
    private Review review;

    @Column(name = "liked_at")
    private LocalDateTime likeAt = LocalDateTime.now();
}
