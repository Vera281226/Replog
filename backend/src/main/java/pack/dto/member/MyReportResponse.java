package pack.dto.member;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyReportResponse {

    private Long id;

    private String targetType; // "콘텐츠", "리뷰", "게시글"
    private String targetTitle; // 신고한 대상 제목
    private String reason; // 신고 사유
    private LocalDateTime createdAt; // 신고한 시각
}
