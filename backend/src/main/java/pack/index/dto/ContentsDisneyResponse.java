package pack.index.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ContentsDisneyResponse DTO
 * - 디즈니+ 인기 콘텐츠 응답 구조
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentsDisneyResponse {
    private Long contentId;
    private String title;
    private String posterPath;
    private String releaseDate;
    private Float rating;
    private Integer voteCount;
}
