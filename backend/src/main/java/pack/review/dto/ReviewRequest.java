package pack.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private Integer contentId;
    private String memberId;
    private String cont;
    private Integer rating;
    private Boolean isSpoiler;
}
