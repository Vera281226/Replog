package pack.controller.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.dto.review.LikeResponse;
import pack.dto.review.ReviewRequest;
import pack.dto.review.ReviewResponse;
import pack.dto.review.ReviewUpdateRequest;
import pack.service.review.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    // 1️⃣ 리뷰 작성
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(
            reviewService.createReview(request)
        );
    }

    // 2️⃣ 리뷰 조회 (contentId, sortType, memberId 명시)
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(
            @RequestParam(name = "contentId") Integer contentId,
            @RequestParam(name = "sortType", required = false, defaultValue = "LATEST") String sortType,
            @RequestParam(name = "memberId", required = false) String memberId
    ) {
        return ResponseEntity.ok(
            reviewService.getReviewsByContentId(contentId, sortType, memberId)
        );
    }

    // 3️⃣ 리뷰 수정
    @PatchMapping(path = "/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable(name = "id") Integer id,
            @RequestBody ReviewUpdateRequest request
    ) {
        request.setReviewId(id);
        return ResponseEntity.ok(
            reviewService.updateReview(request, request.getMemberId())
        );
    }

    // 4️⃣ 리뷰 삭제
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteReview(
            @PathVariable(name = "id") Integer id
    ) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("리뷰 삭제 완료");
    }

    // 5️⃣ 댓글 작성
    @PostMapping(path = "/{parentId}/comments")
    public ResponseEntity<ReviewResponse> createComment(
            @PathVariable(name = "parentId") Integer parentId,
            @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(
            reviewService.createComment(parentId, request)
        );
    }

    // 6️⃣ 댓글 수정
    @PatchMapping(path = "/{id}/comments")
    public ResponseEntity<ReviewResponse> updateComment(
            @PathVariable(name = "id") Integer id,
            @RequestBody ReviewUpdateRequest request
    ) {
        request.setReviewId(id);
        return ResponseEntity.ok(
            reviewService.updateComment(request, request.getMemberId())
        );
    }

    // 7️⃣ 댓글 삭제
    @DeleteMapping(path = "/{id}/comments")
    public ResponseEntity<String> deleteComment(
            @PathVariable(name = "id") Integer id
    ) {
        reviewService.deleteComment(id);
        return ResponseEntity.ok("댓글 삭제 완료");
    }

    // 8️⃣ 좋아요 토글
    @PostMapping(path = "/{id}/like")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable(name = "id") Integer id,
            @RequestBody LikeRequest request
    ) {
        return ResponseEntity.ok(
            reviewService.toggleLike(id, request.getMemberId())
        );
    }
}
