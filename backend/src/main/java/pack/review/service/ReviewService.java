package pack.review.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pack.review.dto.ReviewRequest;
import pack.review.dto.ReviewResponse;
import pack.review.dto.ReviewUpdateRequest;
import pack.review.entity.Review;
import pack.review.entity.ReviewLike;
import pack.review.entity.ReviewLikeId;
import pack.review.repository.ReviewLikeRepository;
import pack.review.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;


    public ReviewResponse createReview(ReviewRequest request) {
        Review review = new Review();
        review.setContentId(request.getContentId());
        review.setMemberId(request.getMemberId());
        review.setCont(request.getCont());
        review.setRating(request.getRating());
        review.setIsSpoiler(request.getIsSpoiler());

        review.setNested(0);

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    public ReviewResponse updateReview(ReviewUpdateRequest request, String currentUserId) {
        Review review = reviewRepository.findById(request.getNum())
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // 본인만 수정 가능 조건 제거
        review.setCont(request.getCont());
        review.setRating(request.getRating());
        review.setIsSpoiler(request.getIsSpoiler());
        review.setUpdatedAt(LocalDateTime.now());

        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        if (review.getNested() == null || review.getNested() == 0) {
            Integer gnum = review.getGnum() != null ? review.getGnum() : review.getNum();
            reviewRepository.deleteByGnum(gnum); // 댓글 포함 삭제
        }
        reviewRepository.deleteById(reviewId);
    }

    public List<ReviewResponse> getReviewsByContentId(Integer contentId, String sortType, String memberId) {
        List<Review> reviews;

        switch (sortType.toUpperCase()) {
            case "RATING":
                reviews = reviewRepository.findTopLevelByContentIdOrderByRatingDesc(contentId);
                break;
            case "LATEST":
                reviews = reviewRepository.findTopLevelByContentIdOrderByCreatedAtDesc(contentId);
                break;
            default:
                reviews = reviewRepository.findAllByContentIdSorted(contentId);
                break;
        }

        // 내가 쓴 리뷰는 따로 분리
        List<Review> myReview = reviews.stream()
                .filter(r -> r.getMemberId().equals(memberId) && r.getNested() == 0)
                .toList();

        // 나머지 리뷰는 제외하고 남김
        List<Review> others = reviews.stream()
                .filter(r -> !r.getMemberId().equals(memberId) || r.getNested() != 0)
                .toList();

        // 내가 쓴 리뷰를 맨 위에 붙이기
        List<Review> finalList = new ArrayList<>();
        finalList.addAll(myReview);
        finalList.addAll(others);

        return finalList.stream()
                .map(this::toResponse)
                .toList();
    }


    public ReviewResponse toResponse(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setNum(review.getNum());
        dto.setMemberId(review.getMemberId());
        dto.setCont(review.getCont());
        dto.setRating(review.getRating());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setLikeCount(review.getLikeCount());
        dto.setIsSpoiler(review.getIsSpoiler());
        dto.setIsEdited(!review.getCreatedAt().equals(review.getUpdatedAt()));
        return dto;
    }

    // 댓글 등록 로직 추가
    public ReviewResponse createComment(Integer parentId, ReviewRequest request) {
        // 부모 리뷰 존재 여부 확인
        Review parentReview = reviewRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("부모 리뷰가 존재하지 않습니다."));
        Review comment = new Review();
        comment.setContentId(parentReview.getContentId());  // 부모 리뷰의 contentId 따라감
        comment.setMemberId(request.getMemberId());
        comment.setCont(request.getCont());
        comment.setRating(null);    // 댓글 별점 없음
        comment.setIsSpoiler(false);    // 댓글 스포 없음
        comment.setGnum(parentReview.getGnum() != null ? parentReview.getGnum() : parentReview.getNum());
        comment.setNested(1);

        Review saved = reviewRepository.save(comment);
        return toResponse(saved);
    }

    // 댓글 수정 로직 추가
    public ReviewResponse updateComment(ReviewUpdateRequest request, String currentUserId) {
        Review review = reviewRepository.findById(request.getNum())
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (review.getNested() == null || review.getNested() != 1)
            throw new RuntimeException("이건 댓글이 아닙니다.");

        review.setCont(request.getCont());
        review.setUpdatedAt(LocalDateTime.now());
        return toResponse(reviewRepository.save(review));
    }

    // 댓글 삭제 로직 추가
    public void deleteComment(Integer commentId) {
        Review review = reviewRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (review.getNested() == null || review.getNested() != 1)
            throw new RuntimeException("이건 댓글이 아닙니다.");

        reviewRepository.deleteById(commentId);
    }

    // 좋아요 토글 로직
    @Transactional
    public boolean toggleLike(String memberId, Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        boolean isLiked;

        if (reviewLikeRepository.existsByMemberIdAndReviewId(memberId, reviewId)) {
            reviewLikeRepository.deleteByMemberIdAndReviewId(memberId, reviewId);
            review.setLikeCount(review.getLikeCount() - 1);
            isLiked = false;
        } else {
            ReviewLike like = new ReviewLike();
            like.setMemberId(memberId);
            like.setReviewId(reviewId);
            like.setReview(review);
            reviewLikeRepository.save(like);
            review.setLikeCount(review.getLikeCount() + 1);
            isLiked = true;
        }

        reviewRepository.save(review);
        return isLiked;
    }
}
