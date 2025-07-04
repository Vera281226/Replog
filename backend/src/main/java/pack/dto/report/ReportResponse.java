package pack.dto.report;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import pack.model.report.Report;
import pack.model.report.Report.TargetType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String targetContent;
    private String targetTitle;
    private String targetAuthor;
    private LocalDateTime targetCreatedAt;
    private Integer targetRating;

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