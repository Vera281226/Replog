package pack.model.member;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberTag {
	// 복합 기본키를 사용함 (member_id + genre_id)
    @EmbeddedId 
    private MemberTagId id;
    
    // 이 관심장르가 어떤 회원의 것인지 나타냄
    // @MapsId("memberId") → 복합키 클래스(MemberTagId)의 memberId 필드와 이 member 객체를 연결
    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    // 이 관심장르가 어떤 장르인지 나타냄
    // @MapsId("genreId") → 복합키 클래스(MemberTagId)의 genreId 필드와 이 genre 객체를 연결
    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    private Genre genre;
}
