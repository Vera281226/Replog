package pack.repository.report;

import pack.model.report.Report;
import pack.model.report.Report.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    
    Page<Report> findByIsProcessedOrderByCreatedAtDesc(Boolean isProcessed, Pageable pageable);
    
    Page<Report> findByTargetTypeOrderByCreatedAtDesc(TargetType targetType, Pageable pageable);
    
    List<Report> findByTargetTypeAndTargetId(TargetType targetType, String targetId);
    
    @Query("SELECT r FROM Report r WHERE r.reporter.id = :reporterId ORDER BY r.createdAt DESC")
    Page<Report> findByReporterIdOrderByCreatedAtDesc(@Param("reporterId") String reporterId, Pageable pageable);
    
    Long countByIsProcessed(Boolean isProcessed);
    
    Long countByTargetTypeAndIsProcessed(TargetType targetType, Boolean isProcessed);
}