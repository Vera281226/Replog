package pack.response.report;

import pack.entity.report.Report;
import pack.entity.report.Report.TargetType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {
    private Integer reportId;
    private String reporterNickname;
    private TargetType targetType;
    private String targetId;
    private String reason;
    private String description;
    private LocalDateTime createdAt;
    private Boolean isProcessed;
    private LocalDateTime processedAt;
    private String processorNickname;
    
    // 신고 대상 원본 콘텐츠 정보 추가
    private String targetContent;      // 신고된 콘텐츠 내용
    private String targetTitle;        // 게시글/파티 제목 (해당하는 경우)
    private String targetAuthor;       // 원본 작성자 닉네임
    private LocalDateTime targetCreatedAt;  // 원본 콘텐츠 작성일
    private Integer targetRating;      // 리뷰 평점 (리뷰인 경우)

    public static ReportResponse fromEntity(Report report) {
        return ReportResponse.builder()
                .reportId(report.getReportId())
                .reporterNickname(report.getReporter().getNickname())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .description(report.getDescription())
                .createdAt(report.getCreatedAt())
                .isProcessed(report.getIsProcessed())
                .processedAt(report.getProcessedAt())
                .processorNickname(
                        report.getProcessor() != null ?
                        report.getProcessor().getNickname() : null)
                .build();
    }
}