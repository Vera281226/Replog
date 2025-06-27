package pack.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.review.entity.ReviewLike;
import pack.review.entity.ReviewLikeId;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {
    boolean existsByMemberIdAndReviewId(String memberId, Integer reviewId);
    void deleteByMemberIdAndReviewId(String memberId, Integer reviewId);
}
