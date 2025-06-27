package pack.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {
    private Integer num;
    private String memberId;
    private String cont;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    private Boolean isSpoiler;
    private Boolean isEdited; // (수정됨) 표시용
}
