package pack.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequest {
    private Integer num;
    private String cont;
    private Integer rating;
    private Boolean isSpoiler;
    private String memberId;
}
