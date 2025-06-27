package pack.dto.member;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyReviewResponse {

    private Long reviewId;

    private String movieTitle;

    private String content;

    private int rating;

    private LocalDateTime createdAt;
}
