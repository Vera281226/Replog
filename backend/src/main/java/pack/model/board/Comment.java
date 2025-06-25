package pack.model.board;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentNo;

    @Column(nullable = false, length = 20)
    private String id; // FK - member.id

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    private Integer postNo; // FK - posts.post_no

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isHidden = false;

    @Column(nullable = false)
    private Integer likes = 0;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}