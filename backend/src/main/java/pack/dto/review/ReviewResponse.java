package pack.dto.review;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {
    private Integer reviewId;
    private Integer gnum;
    private String memberId;
    private Integer contentId;
    private String cont;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    private Boolean isSpoiler;
    private Boolean isEdited; // (수정됨) 표시용
    private Boolean isLiked;
}
