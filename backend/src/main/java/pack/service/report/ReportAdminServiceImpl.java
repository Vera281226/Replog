package pack.service.report;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pack.repository.board.PostRepository;
import pack.repository.chat.ChatMessageRepository;
import pack.repository.member.MemberRepository;
import pack.repository.report.ReportRepository;
import pack.repository.review.ReviewRepository;
import pack.repository.theater.PartyPostRepository;
import pack.dto.report.ReportResponse;
import pack.model.member.Member;
import pack.model.report.Report;
import pack.model.report.Report.TargetType;
import pack.modules.contents.repository.ContentsRepository;

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

    public ReportResponse processReport(Integer reportId, String processorId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다"));
        Member processor = memberRepository.findById(processorId)
                .orElseThrow(() -> new IllegalArgumentException("처리자를 찾을 수 없습니다"));

        report.setIsProcessed(true);
        report.setProcessedAt(LocalDateTime.now());
        report.setProcessor(processor);

        return reportResponseMapper.convertToResponse(report);
    }
    /* 처리 취소 */
    public ReportResponse cancelProcess(Integer reportId, String processorId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다"));
        // 처리 취소할 때도 권한/로그 남기고 싶다면 processorId 활용 가능
        report.setIsProcessed(false);
        report.setProcessedAt(null);
        report.setProcessor(null);

        return reportResponseMapper.convertToResponse(report);
    }
    private final PostRepository postRepository;
    private final PartyPostRepository partyPostRepository;
    private final ReviewRepository reviewRepository;
    private final ContentsRepository contentRepository;
    private final ChatMessageRepository chatMessageRepository;
    
    @Override
    public ReportResponse processReportAndDelete(Integer reportId, String processorId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다"));

        if (report.getTargetType() == TargetType.USER) {
            throw new UnsupportedOperationException("USER는 삭제할 수 없습니다");
        }

        if (report.getTargetType() == TargetType.CONTENT_REQUEST) {
            // 삭제 없이 신고 처리만 수행
            Member processor = memberRepository.findById(processorId)
                .orElseThrow(() -> new IllegalArgumentException("처리자를 찾을 수 없습니다"));
            report.setIsProcessed(true);
            report.setProcessedAt(LocalDateTime.now());
            report.setProcessor(processor);
            return reportResponseMapper.convertToResponse(report);
        }

        // 실제 삭제가 필요한 타입만 분기
        switch (report.getTargetType()) {
            case POST:
                postRepository.deleteById(Integer.parseInt(report.getTargetId()));
                break;
            case REVIEW:
                reviewRepository.deleteById(Integer.parseInt(report.getTargetId()));
                break;
            case CONTENT:
                contentRepository.deleteById(Integer.parseInt(report.getTargetId()));
                break;
            case CHAT_MESSAGE:
                chatMessageRepository.deleteById(Integer.parseInt(report.getTargetId()));
                break;
            default:
                throw new IllegalArgumentException("삭제 지원하지 않는 타입");
        }

        Member processor = memberRepository.findById(processorId)
            .orElseThrow(() -> new IllegalArgumentException("처리자를 찾을 수 없습니다"));
        report.setIsProcessed(true);
        report.setProcessedAt(LocalDateTime.now());
        report.setProcessor(processor);

        return reportResponseMapper.convertToResponse(report);
    }

}
