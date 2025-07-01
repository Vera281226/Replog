package pack.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
