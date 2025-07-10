package pack.repository.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pack.entity.review.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 평점 높은 순 (리뷰만 — 댓글 제외: reviewId == gnum인 것만)
    @Query("SELECT r FROM Review r WHERE r.contentId = :contentId AND r.reviewId = r.gnum ORDER BY r.rating DESC")
    List<Review> findTopLevelByContentIdOrderByRatingDesc(@Param("contentId") Integer contentId);

    // 최신 순 (리뷰만 — 댓글 제외: reviewId == gnum)
    @Query("SELECT r FROM Review r WHERE r.contentId = :contentId AND r.reviewId = r.gnum ORDER BY r.createdAt DESC")
    List<Review> findTopLevelByContentIdOrderByCreatedAtDesc(@Param("contentId") Integer contentId);

    // 전체 (리뷰 + 댓글), 댓글은 gnum 기준 정렬, createdAt으로 보조 정렬
    @Query("SELECT r FROM Review r WHERE r.contentId = :contentId ORDER BY r.gnum ASC, r.createdAt ASC")
    List<Review> findAllByContentIdSorted(@Param("contentId") Integer contentId);

    // gnum 기준으로 전체 삭제 (리뷰와 해당 댓글들 한 번에 삭제)
    void deleteByGnum(Integer gnum);

    @Query("SELECT r FROM Review r WHERE r.gnum = :reviewId AND r.reviewId <> r.gnum")
    List<Review> findByGnum(@Param("reviewId") Integer reviewId);

    List<Review> findByContentIdOrderByCreatedAtDesc(Integer contentId);

    List<Review> findByContentIdOrderByRatingDesc(Integer contentId);

    @Query("SELECT r FROM Review r WHERE r.contentId = :contentId")
    List<Review> findByContentId(@Param("contentId") Integer contentId);
    
    Page<Review> findByMemberIdOrderByCreatedAtDesc(String memberId, Pageable pageable);
}
