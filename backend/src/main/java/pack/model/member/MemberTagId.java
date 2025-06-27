package pack.model.member;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberTagId implements Serializable {

    private String memberId;

    private Long genreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberTagId that)) return false;
        return Objects.equals(memberId, that.memberId) &&
               Objects.equals(genreId, that.genreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, genreId);
    }
}
