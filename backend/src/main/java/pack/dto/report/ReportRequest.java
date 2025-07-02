package pack.dto.report;

import pack.entity.report.Report;
import pack.entity.report.Report.TargetType;
import pack.model.member.Member;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequest {
    
//    @NotNull(message = "신고 대상 유형은 필수입니다")
    private TargetType targetType;
    
    private String targetId;
   
//    @NotBlank(message = "신고 사유는 필수입니다")
//    @Size(max = 100, message = "신고 사유는 100자 이내로 입력해주세요")
    private String reason;
    
//    @Size(max = 1000, message = "상세 설명은 1000자 이내로 입력해주세요")
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