package pack.service.report;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pack.repository.member.MemberRepository;
import pack.repository.report.ReportRepository;
import pack.dto.report.ReportResponse;
import pack.model.member.Member;
import pack.model.report.Report;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportAdminServiceImpl implements ReportAdminService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final ReportResponseMapper reportResponseMapper;

    public Page<ReportResponse> getReports(Pageable pageable) {
        return reportRepository.findAll(pageable)
            .map(reportResponseMapper::convertToResponse);
    }

    public Page<ReportResponse> getUnprocessedReports(Pageable pageable) {
        return reportRepository.findByIsProcessedOrderByCreatedAtDesc(false, pageable)
            .map(reportResponseMapper::convertToResponse);
    }

    public Page<ReportResponse> getReportsByType(Report.TargetType targetType, Pageable pageable) {
        return reportRepository.findByTargetTypeOrderByCreatedAtDesc(targetType, pageable)
            .map(reportResponseMapper::convertToResponse);
    }

    @Transactional
    public ReportResponse processReport(Integer reportId, String processorId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다"));
        Member processor = memberRepository.findById(processorId)
            .orElseThrow(() -> new IllegalArgumentException("처리자를 찾을 수 없습니다"));
        report.setIsProcessed(true);
        report.setProcessedAt(LocalDateTime.now());
        report.setProcessor(processor);
        Report processedReport = reportRepository.save(report);
        return reportResponseMapper.convertToResponse(processedReport);
    }
}
