package pack.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pack.review.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 평점 높은 순 (nested == 0만 포함)
    @Query("SELECT r FROM Review r WHERE r.contentId = :contentId AND r.nested = 0 ORDER BY r.rating DESC")
    List<Review> findTopLevelByContentIdOrderByRatingDesc(@Param("contentId") Integer contentId);

    // 최신 순 (nested == 0만 포함)
    @Query("SELECT r FROM Review r WHERE r.contentId = :contentId AND r.nested = 0 ORDER BY r.createdAt DESC")
    List<Review> findTopLevelByContentIdOrderByCreatedAtDesc(@Param("contentId") Integer contentId);

    // gnum, nested, 작성일 기준 정렬 (댓글 포함 전체 정렬)
    @Query("SELECT r FROM Review r WHERE r.contentId = :contentId ORDER BY r.gnum ASC, r.nested ASC, r.createdAt ASC")
    List<Review> findAllByContentIdSorted(@Param("contentId") Integer contentId);

    // 그룹으로 댓글 포함 전체 삭제
    void deleteByGnum(Integer gnum);
}
