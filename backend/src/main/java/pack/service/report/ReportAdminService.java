package pack.service.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pack.dto.report.ReportResponse;
import pack.model.report.Report.TargetType;

public interface ReportAdminService {
	Page<ReportResponse> getReports(Pageable pageable);
    Page<ReportResponse> getUnprocessedReports(Pageable pageable);
    Page<ReportResponse> getReportsByType(TargetType targetType, Pageable pageable);
    ReportResponse processReport(Integer reportId, String processorId);
    ReportResponse cancelProcess(Integer reportId, String processorId); // ✅ 
    ReportResponse processReportAndDelete(Integer reportId, String processorId);
}