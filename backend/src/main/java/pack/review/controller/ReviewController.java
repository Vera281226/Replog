package pack.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.review.service.ReviewService;
import pack.review.dto.ReviewRequest;
import pack.review.dto.ReviewResponse;
import pack.review.dto.ReviewUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성 (요청에서 memberId 직접 받아옴)
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }

    // 리뷰 조회 (contentId 기준 + 정렬 옵션)
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(
            @RequestParam Integer contentId,
            @RequestParam(required = false, defaultValue = "LATEST") String sortType,
            @RequestParam(required = false) String memberId // 로그인된 사용자 ID
    ) {
        return ResponseEntity.ok(reviewService.getReviewsByContentId(contentId, sortType, memberId));
    }


    // 리뷰 수정 (요청 바디에 memberId 포함 필요)
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable("id") Integer id,
            @RequestBody ReviewUpdateRequest request
    ) {
        request.setNum(id);
        return ResponseEntity.ok(reviewService.updateReview(request, request.getMemberId()));
    }

    // 리뷰 삭제 (요청 파라미터로 memberId 전달)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);  // memberId 파라미터 제거
        return ResponseEntity.ok("리뷰 삭제 완료");
    }

    // 리뷰의 댓글 작성
    @PostMapping("{parentId}/comments")
    public ResponseEntity<ReviewResponse> createComment(
            @PathVariable Integer parentId,
            @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(reviewService.createComment(parentId, request));
    }

    // 리뷰의 댓글 수정
    @PatchMapping("/{id}/comments")
    public ResponseEntity<ReviewResponse> updateComment (
            @PathVariable("id") Integer id,
            @RequestBody ReviewUpdateRequest request
    ) {
        request.setNum(id);
        return ResponseEntity.ok(reviewService.updateComment(request, request.getMemberId()));
    }

    // 리뷰의 댓글 삭제

    @DeleteMapping("/{id}/comments")
    public ResponseEntity<String> deleteComment(@PathVariable Integer id) {
        reviewService.deleteComment(id);
        return ResponseEntity.ok("댓글 삭제 완료");
    }

    // 좋아요 토글 컨트롤러 추가
    @PostMapping("{id}/like")
    public ResponseEntity<String> toggleLike(
            @PathVariable("id") Integer reviewId,
            @RequestParam("memberId") String memberId
    ) {
        boolean liked = reviewService.toggleLike(memberId, reviewId);
        return ResponseEntity.ok(liked ? "좋아요 추가" : "좋아요 취소");
    }
}
