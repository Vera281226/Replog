package pack.service.review;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pack.dto.review.LikeResponse;
import pack.dto.review.ReviewRequest;
import pack.dto.review.ReviewResponse;
import pack.dto.review.ReviewUpdateRequest;
import pack.entity.review.Review;
import pack.entity.review.ReviewLike;
import pack.repository.review.ReviewLikeRepository;
import pack.repository.review.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        Review review = new Review();
        review.setContentId(request.getContentId());
        review.setMemberId(request.getMemberId());
        review.setCont(request.getCont());
        review.setRating(request.getRating());
        review.setIsSpoiler(request.getIsSpoiler());

        // 첫 저장
        Review saved = reviewRepository.save(review);

        // gnum 지정 후 업데이트
        saved.setGnum(saved.getReviewId());

        return toResponse(reviewRepository.save(saved), request.getMemberId());
    }


    public ReviewResponse updateReview(ReviewUpdateRequest request, String currentUserId) {
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setCont(request.getCont());
        review.setRating(request.getRating());
        review.setIsSpoiler(request.getIsSpoiler());
        review.setUpdatedAt(LocalDateTime.now());

        return toResponse(reviewRepository.save(review), currentUserId);
    }

    @Transactional
    public void deleteReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        reviewRepository.deleteByGnum(reviewId);
    }

    public List<ReviewResponse> getReviewsByContentId(
            Integer contentId, String sortType, String memberId,
            int page, int size
    ) {
        if (sortType == null || sortType.isBlank()) {
            sortType = "LATEST";
        }

        List<Review> reviews;

        switch (sortType.toUpperCase()) {
            case "RATING":
                reviews = reviewRepository.findByContentIdOrderByRatingDesc(contentId);
                break;
            case "LATEST":
            default:
                reviews = reviewRepository.findByContentIdOrderByCreatedAtDesc(contentId);
                break;
        }

        // 페이징 적용 (subList)
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, reviews.size());

        if (fromIndex >= reviews.size()) {
            return List.of(); // 더 이상 데이터 없을 때 빈 리스트 반환
        }

        return reviews.subList(fromIndex, toIndex).stream()
                .map(review -> toResponse(review, memberId))
                .toList();
    }



    @Transactional
    public ReviewResponse createComment(Integer parentId, ReviewRequest request) {
        Review parentReview = reviewRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("부모 리뷰가 존재하지 않습니다."));

        Review comment = new Review();
        comment.setContentId(parentReview.getContentId());
        comment.setMemberId(request.getMemberId());
        comment.setCont(request.getCont());
        comment.setRating(null);
        comment.setIsSpoiler(false);
        comment.setGnum(parentReview.getGnum() != null ? parentReview.getGnum() : parentReview.getReviewId());

        Review saved = reviewRepository.save(comment);
        return toResponse(saved, request.getMemberId());
    }

    public ReviewResponse updateComment(ReviewUpdateRequest request, String currentUserId) {
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        review.setCont(request.getCont());
        review.setUpdatedAt(LocalDateTime.now());
        return toResponse(reviewRepository.save(review), currentUserId);
    }

    @Transactional
    public void deleteComment(Integer commentId) {
        Review comment = reviewRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        // 댓글인지 확인
        if (comment.getReviewId().equals(comment.getGnum())) {
            throw new RuntimeException("이 ID는 댓글이 아닌 리뷰입니다.");
        }

        reviewRepository.deleteById(commentId);
    }


    public ReviewResponse toResponse(Review review, String memberId) {
        ReviewResponse dto = new ReviewResponse();
        dto.setReviewId(review.getReviewId());
        dto.setContentId(review.getContentId());
        dto.setMemberId(review.getMemberId());
        dto.setCont(review.getCont());
        dto.setGnum(review.getGnum());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setIsSpoiler(review.getIsSpoiler());
        dto.setIsEdited(!review.getCreatedAt().equals(review.getUpdatedAt()));


        // 좋아요 관련
        boolean liked = (memberId != null) && reviewLikeRepository.existsByMemberIdAndReviewId(memberId, review.getReviewId());
        int likeCount = reviewLikeRepository.countByReviewId(review.getReviewId());
        dto.setIsLiked(liked);
        dto.setLikeCount(likeCount);

        return dto;
    }

    @Transactional
    public LikeResponse toggleLike(Integer reviewId, String memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않음"));

        boolean isLiked;

        if (reviewLikeRepository.existsByMemberIdAndReviewId(memberId, reviewId)) {
            reviewLikeRepository.deleteByMemberIdAndReviewId(memberId, reviewId);
            isLiked = false;
        } else {
            ReviewLike like = new ReviewLike();
            like.setMemberId(memberId);
            like.setReviewId(reviewId);
            like.setReview(review);
            reviewLikeRepository.save(like);
            isLiked = true;
        }

        int likeCount = reviewLikeRepository.countByReviewId(reviewId);
        return new LikeResponse(isLiked, likeCount);
    }

    public double getAverageRating(Integer contentId) {
        List<Review> reviews = reviewRepository.findByContentId(contentId);

        // 본 리뷰만 (gnum == reviewId) + 평점 있는 것만
        List<Review> valid = reviews.stream()
                .filter(r -> r.getGnum() != null && r.getGnum().equals(r.getReviewId()))
                .filter(r -> r.getRating() != null)
                .collect(Collectors.toList());

        if (valid.isEmpty()) return 0.0;

        double sum = valid.stream().mapToDouble(Review::getRating).sum();
        return sum / valid.size();
    }
    

    public Page<ReviewResponse> getReviewsByMember(String memberId, Pageable pageable) {
        return reviewRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(review -> toResponse(review, memberId));
    }

}
