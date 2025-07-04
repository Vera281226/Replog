package pack.service.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pack.dto.report.ReportRequest;
import pack.dto.report.ReportResponse;

public interface ReportService {
    ReportResponse createReport(String reporterId, ReportRequest request);
    ReportResponse getReport(Integer reportId);
    Page<ReportResponse> getMyReports(String reporterId, Pageable pageable);
}