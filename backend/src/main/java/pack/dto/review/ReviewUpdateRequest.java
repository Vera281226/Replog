package pack.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequest {
    private Integer reviewId;
    private String cont;
    private Integer rating;
    private Boolean isSpoiler;
    private String memberId;
}
