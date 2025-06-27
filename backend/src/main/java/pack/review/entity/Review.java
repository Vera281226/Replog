package pack.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer num;

    private Integer contentId;
    private String memberId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer gnum;
    private Integer nested;
    private Integer rating;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;


    private String cont;
    private Boolean isSpoiler;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
