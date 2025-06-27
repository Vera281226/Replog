package pack.model.member;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    private String id;

    private String password;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthdate;
    private String gender;
    private String role;
    
    @CreatedDate  // 이것도 누락되어 있었음
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 기본값 설정
    
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @ManyToMany
    @JoinTable(
        name = "member_tag",
        joinColumns = @JoinColumn(name = "member_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;
}