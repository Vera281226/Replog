package pack.dto.report;

import pack.model.member.Member;
import pack.model.report.Report;
import pack.model.report.Report.TargetType;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    
    private TargetType targetType;
    private String targetId;
    private String reason;
    private String description;

    public Report toEntity(Member reporter) {
        return Report.builder()
                     .reporter(reporter)
                     .targetType(targetType)
                     .targetId(targetId)
                     .reason(reason)
                     .description(description)
                     .createdAt(LocalDateTime.now())
                     .isProcessed(false)
                     .build();
    }
}