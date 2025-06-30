package pack.entity.review;

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
    @Column(name = "review_id")
    private Integer reviewId;

    private Integer contentId;
    private String memberId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(name = "gnum")
    private Integer gnum;
    private Integer rating;

    private String cont;

    @Column(name = "is_spoiler")
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
