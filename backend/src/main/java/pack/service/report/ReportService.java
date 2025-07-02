package pack.service.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import pack.dto.report.ReportRequest;
import pack.entity.report.Report.TargetType;
import pack.response.report.ReportResponse;

public interface ReportService {
    ReportResponse createReport(String reporterId, ReportRequest request);
    ReportResponse getReport(Integer reportId);
    Page<ReportResponse> getReports(Pageable pageable);
    Page<ReportResponse> getUnprocessedReports(Pageable pageable);
    Page<ReportResponse> getReportsByType(TargetType targetType, Pageable pageable);
    Page<ReportResponse> getMyReports(String reporterId, Pageable pageable);
    ReportResponse processReport(Integer reportId, String processorId);
}