package pack.index.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import pack.entity.review.Review;
import pack.index.dto.IndexHotReviewResponse;

import java.util.List;

/**
 * IndexReviewRepository
 * -------------------------------------------------------------
 * ○ index.html의 "지금 뜨는 리뷰" 섹션 전용 Repository
 * ○ 좋아요 수 기준으로 상위 리뷰 조회 (Review + Contents + ReviewLike)
 * ○ Pageable로 상위 N개 제한
 * ○ 대댓글 제외 → gnum = reviewId인 원본 리뷰만 포함
 */
public interface IndexReviewRepository extends Repository<Review, Integer> {

    /**
     * 지금 뜨는 리뷰 상위 N개 조회
     * - 좋아요 수 기준 내림차순 정렬
     * - IndexHotReviewResponse로 직접 DTO 매핑
     * - 대댓글 제외 (gnum = reviewId 조건으로 원본만 조회)
     */
    @Query("""
        SELECT new pack.index.dto.IndexHotReviewResponse(
            r.reviewId,
            r.memberId,
            c.title,
            c.posterPath,
            r.cont,
            r.rating,
            COUNT(rl.reviewId),
            c.contentId
        )
        FROM Review r
        JOIN Contents c ON r.contentId = c.contentId
        LEFT JOIN ReviewLike rl ON rl.reviewId = r.reviewId
        WHERE r.gnum = r.reviewId
        GROUP BY r.reviewId, r.memberId, c.title, c.posterPath, r.cont, r.rating, c.contentId
        ORDER BY COUNT(rl.reviewId) DESC
    """)
    List<IndexHotReviewResponse> findTopReviews(Pageable pageable);
}
