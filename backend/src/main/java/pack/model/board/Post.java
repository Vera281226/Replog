package pack.model.board;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postNo;

    @Column(nullable = false, length = 20)
    private String memberId;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer views = 0;
    
    @Column(nullable = false)
    private Integer likes = 0;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    public void increaseLikes() {
        this.likes = this.likes + 1;
    }

    public void decreaseLikes() {
        if (this.likes > 0) {
            this.likes = this.likes - 1;
        }
    }
    
    public void increaseViews() {
        this.views = this.views + 1;
    }
}