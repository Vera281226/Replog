package pack.model.theater;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "party_posts")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class PartyPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_post_no")
    private Integer partyPostNo;

    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 50)
    private String movie;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "party_deadline", nullable = false)
    private LocalDateTime partyDeadline;

    @Column(name = "is_terminated")
    private Boolean isTerminated = false;

    @Column(name = "is_hidden")
    private Boolean isHidden = false;

    private Integer views;

    @Column(name = "theater_id", nullable = false)
    private Integer theaterId;

    @Column(name = "party_limit", nullable = false)
    private Integer partyLimit;

    @Column(length = 2)
    private String gender;

    @Column(name = "age_groups_mask")
    private Integer ageGroupsMask;
}