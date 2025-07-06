package pack.controller.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import lombok.RequiredArgsConstructor;

import pack.dto.report.ReportRequest;
import pack.dto.report.ReportResponse;
import pack.service.report.ReportService;
import pack.util.AuthUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
        @RequestBody ReportRequest request
    ) {
    	String reporterId = AuthUtil.getCurrentMemberId();
        return ResponseEntity.ok(reportService.createReport(reporterId, request));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ReportResponse>> getMyReports(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
    	String reporterId = AuthUtil.getCurrentMemberId();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reportService.getMyReports(reporterId, pageable));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponse> getReport(
        @PathVariable(name = "reportId") Integer reportId
    ) {
        return ResponseEntity.ok(reportService.getReport(reportId));
    }
}