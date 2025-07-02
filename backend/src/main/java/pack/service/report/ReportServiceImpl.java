package pack.service.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import pack.repository.report.ReportRepository;
import pack.repository.review.ReviewRepository;
import pack.repository.theater.PartyPostRepository;
import pack.dto.report.ReportRequest;
import pack.entity.report.Report;
import pack.entity.review.Review;
import pack.model.board.Post;
import pack.model.chat.ChatMessage;
import pack.model.member.Member;
import pack.model.theater.PartyPost;
import pack.repository.board.PostRepository;
import pack.repository.chat.ChatMessageRepository;
import pack.repository.member.MemberRepository;
import pack.response.report.ReportResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PartyPostRepository partyPostRepository;
    private final ReviewRepository reviewRepository;
    private final ChatMessageRepository chatMessageRepository;

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
        return convertToResponse(savedReport);
    }

    public Page<ReportResponse> getReports(Pageable pageable) {
        return reportRepository.findAll(pageable)
            .map(this::convertToResponse);
    }

    public Page<ReportResponse> getUnprocessedReports(Pageable pageable) {
        return reportRepository.findByIsProcessedOrderByCreatedAtDesc(false, pageable)
            .map(this::convertToResponse);
    }

    public Page<ReportResponse> getReportsByType(Report.TargetType targetType, Pageable pageable) {
        return reportRepository.findByTargetTypeOrderByCreatedAtDesc(targetType, pageable)
            .map(this::convertToResponse);
    }

    public Page<ReportResponse> getMyReports(String reporterId, Pageable pageable) {
        return reportRepository.findByReporterIdOrderByCreatedAtDesc(reporterId, pageable)
            .map(this::convertToResponse);
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
        return convertToResponse(processedReport);
    }

    public ReportResponse getReport(Integer reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다"));

        return convertToResponse(report);
    }

    private ReportResponse convertToResponse(Report report) {
        ReportResponse.ReportResponseBuilder builder = ReportResponse.builder()
            .reportId(report.getReportId())
            .reporterNickname(report.getReporter().getNickname())
            .targetType(report.getTargetType())
            .targetId(report.getTargetId())
            .reason(report.getReason())
            .description(report.getDescription())
            .createdAt(report.getCreatedAt())
            .isProcessed(report.getIsProcessed())
            .processedAt(report.getProcessedAt())
            .processorNickname(report.getProcessor() != null ? report.getProcessor().getNickname() : null);

        // 신고 대상 원본 콘텐츠 정보 추가
        addTargetContentInfo(builder, report);

        return builder.build();
    }

    private void addTargetContentInfo(ReportResponse.ReportResponseBuilder builder, Report report) {
        if (report.getTargetId() == null) {
            return;
        }

        try {
            switch (report.getTargetType()) {
                case POST:
                    addPostInfo(builder, report.getTargetId());
                    break;
                case PARTY_POST:
                    addPartyPostInfo(builder, report.getTargetId());
                    break;
                case REVIEW:
                    addReviewInfo(builder, report.getTargetId());
                    break;
                case CHAT_MESSAGE:
                    addChatMessageInfo(builder, report.getTargetId());
                    break;
                case USER:
                    addUserInfo(builder, report.getTargetId());
                    break;
                default:
                    // 다른 타입들은 원본 콘텐츠 정보 없음
                    break;
            }
        } catch (Exception e) {
            // 원본 콘텐츠를 찾을 수 없는 경우 (삭제된 경우 등)
            builder.targetContent("원본 콘텐츠를 찾을 수 없습니다 (삭제되었을 수 있음)");
        }
    }

    private void addPostInfo(ReportResponse.ReportResponseBuilder builder, String targetId) {
        Optional<Post> postOpt = postRepository.findById(Integer.parseInt(targetId));
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            builder.targetContent(post.getContent())
                .targetTitle(post.getTitle())
                .targetAuthor(post.getNickname())
                .targetCreatedAt(post.getCreatedAt());
        }
    }

    private void addPartyPostInfo(ReportResponse.ReportResponseBuilder builder, String targetId) {
        Optional<PartyPost> partyPostOpt = partyPostRepository.findById(Integer.parseInt(targetId));
        if (partyPostOpt.isPresent()) {
            PartyPost partyPost = partyPostOpt.get();
            builder.targetContent(partyPost.getContent())
                .targetTitle(partyPost.getTitle())
                .targetAuthor(partyPost.getNickname())
                .targetCreatedAt(partyPost.getCreatedAt());
        }
    }

    private void addReviewInfo(ReportResponse.ReportResponseBuilder builder, String targetId) {
        Optional<Review> reviewOpt = reviewRepository.findById(Integer.parseInt(targetId));
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            builder.targetContent(review.getCont())
                .targetAuthor(review.getMemberId() != null ? review.getMemberId() : "탈퇴한 사용자")
                .targetCreatedAt(review.getCreatedAt())
                .targetRating(review.getRating());
        }
    }

    private void addChatMessageInfo(ReportResponse.ReportResponseBuilder builder, String targetId) {
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(Integer.parseInt(targetId));
        if (messageOpt.isPresent()) {
            ChatMessage message = messageOpt.get();
            builder.targetContent(message.getMessageText())
                .targetAuthor(message.getSender().getNickname())
                .targetCreatedAt(message.getSentAt());
        }
    }

    private void addUserInfo(ReportResponse.ReportResponseBuilder builder, String targetId) {
        Optional<Member> memberOpt = memberRepository.findById(targetId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            builder.targetAuthor(member.getNickname())
                .targetContent("사용자 신고 - ID: " + member.getMemberId())
                .targetCreatedAt(member.getCreatedAt());
        }
    }

}