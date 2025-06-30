package pack.model.member;

import java.io.Serializable;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
//복합키 클래스: member_id와 genre_id를 하나로 묶어서 기본키로 사용할 수 있게 만듦
//JPA에서는 복합키를 쓸 때 반드시 별도 클래스로 만들어야 하며, Serializable을 구현해야 함
public class MemberTagId implements Serializable {
    private String memberId;
    private Integer genreId;
}
