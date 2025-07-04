package pack.service.report;

import java.util.Optional;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import pack.dto.report.ReportResponse;
import pack.model.board.Post;
import pack.model.chat.ChatMessage;
import pack.model.member.Member;
import pack.model.report.Report;
import pack.entity.review.Review;
import pack.model.theater.PartyPost;
import pack.repository.board.PostRepository;
import pack.repository.chat.ChatMessageRepository;
import pack.repository.member.MemberRepository;
import pack.repository.review.ReviewRepository;
import pack.repository.theater.PartyPostRepository;

@Component
@RequiredArgsConstructor
public class ReportResponseMapper {

    private final PostRepository postRepository;
    private final PartyPostRepository partyPostRepository;
    private final ReviewRepository reviewRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    public ReportResponse convertToResponse(Report report) {
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

        addTargetContentInfo(builder, report);
        return builder.build();
    }

    private void addTargetContentInfo(ReportResponse.ReportResponseBuilder builder, Report report) {
        if (report.getTargetId() == null) return;
        try {
            switch (report.getTargetType()) {
                case POST: addPostInfo(builder, report.getTargetId()); break;
                case PARTY_POST: addPartyPostInfo(builder, report.getTargetId()); break;
                case REVIEW: addReviewInfo(builder, report.getTargetId()); break;
                case CHAT_MESSAGE: addChatMessageInfo(builder, report.getTargetId()); break;
                case USER: addUserInfo(builder, report.getTargetId()); break;
                default: break;
            }
        } catch (Exception e) {
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
