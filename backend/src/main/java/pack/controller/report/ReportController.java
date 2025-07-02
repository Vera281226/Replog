package pack.controller.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
//import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pack.dto.report.ReportRequest;
import pack.entity.report.Report.TargetType;
import pack.response.report.ReportResponse;
import pack.service.report.ReportService;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
//            @Valid
            @RequestBody ReportRequest request,
            @SessionAttribute(name = "loginMember") String reporterId) {      // 변경
        ReportResponse response = reportService.createReport(reporterId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getReports(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportResponse> reports = reportService.getReports(pageable);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/unprocessed")
    public ResponseEntity<Page<ReportResponse>> getUnprocessedReports(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportResponse> reports = reportService.getUnprocessedReports(pageable);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/type/{targetType}")
    public ResponseEntity<Page<ReportResponse>> getReportsByType(
            @PathVariable("targetType") TargetType targetType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportResponse> reports = reportService.getReportsByType(targetType, pageable);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ReportResponse>> getMyReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @SessionAttribute(name = "loginMember") String reporterId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReportResponse> reports = reportService.getMyReports(reporterId, pageable);
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{reportId}/process")
    public ResponseEntity<ReportResponse> processReport(
            @PathVariable Integer reportId,
            @SessionAttribute(name = "loginMember") String processorId) {     // 변경
        ReportResponse response = reportService.processReport(reportId, processorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponse> getReport(
            @PathVariable("reportId") Integer reportId) {
        ReportResponse response = reportService.getReport(reportId);
        return ResponseEntity.ok(response);
    }
}