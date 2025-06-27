package pack.dto.member;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreUpdateRequest {

    private String memberId;
    private List<Long> genreIds;
}
