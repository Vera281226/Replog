package pack.service.report;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pack.repository.member.MemberRepository;
import pack.repository.report.ReportRepository;
import pack.dto.report.ReportRequest;
import pack.dto.report.ReportResponse;
import pack.model.member.Member;
import pack.model.report.Report;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final ReportResponseMapper reportResponseMapper;

    @Transactional
    public ReportResponse createReport(String reporterId, ReportRequest request) {
        Member reporter = memberRepository.findById(reporterId)
            .orElseThrow(() -> new IllegalArgumentException("신고자를 찾을 수 없습니다"));
        Report report = Report.builder()
            .reporter(reporter)
            .targetType(request.getTargetType())
            .targetId(request.getTargetId())
            .reason(request.getReason())
            .description(request.getDescription())
            .createdAt(LocalDateTime.now())
            .isProcessed(false)
            .build();
        Report savedReport = reportRepository.save(report);
        return reportResponseMapper.convertToResponse(savedReport);
    }

    public ReportResponse getReport(Integer reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다"));
        return reportResponseMapper.convertToResponse(report);
    }

    public Page<ReportResponse> getMyReports(String reporterId, Pageable pageable) {
        return reportRepository.findByReporterIdOrderByCreatedAtDesc(reporterId, pageable)
            .map(reportResponseMapper::convertToResponse);
    }
}