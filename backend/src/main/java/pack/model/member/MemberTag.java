package pack.model.member;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "member_tag")
public class MemberTag {

    @EmbeddedId
    private MemberTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    private Genre genre;
}
