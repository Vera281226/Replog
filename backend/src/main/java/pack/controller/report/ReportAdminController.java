package pack.controller.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pack.dto.report.ReportResponse;
import pack.model.report.Report.TargetType;
import pack.service.report.ReportAdminService;
import pack.util.AuthUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reports")
public class ReportAdminController {

    private final ReportAdminService reportAdminService;

    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getReports(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reportAdminService.getReports(pageable));
    }

    @GetMapping("/unprocessed")
    public ResponseEntity<Page<ReportResponse>> getUnprocessedReports(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reportAdminService.getUnprocessedReports(pageable));
    }

    @GetMapping("/type/{targetType}")
    public ResponseEntity<Page<ReportResponse>> getReportsByType(
        @PathVariable(name = "targetType") TargetType targetType,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reportAdminService.getReportsByType(targetType, pageable));
    }

    @PutMapping("/{reportId}/process")
    public ResponseEntity<ReportResponse> processReport(
        @PathVariable(name = "reportId") Integer reportId
    ) {
    	String processorId = AuthUtil.getCurrentMemberId();
        return ResponseEntity.ok(reportAdminService.processReport(reportId, processorId));
    }
}