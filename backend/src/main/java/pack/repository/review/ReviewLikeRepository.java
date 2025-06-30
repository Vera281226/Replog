package pack.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.review.ReviewLike;
import pack.entity.review.ReviewLikeId;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {
    boolean existsByMemberIdAndReviewId(String memberId, Integer reviewId);
    void deleteByMemberIdAndReviewId(String memberId, Integer reviewId);
    int countByReviewId(Integer reviewId);
}
