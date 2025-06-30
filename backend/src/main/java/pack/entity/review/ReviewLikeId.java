package pack.entity.review;

import java.io.Serializable;
import java.util.Objects;

public class ReviewLikeId implements Serializable {
    private String memberId;
    private Integer reviewId;

    // 기본 생성자, equals, hashCode 필수
    public ReviewLikeId() {}

    public ReviewLikeId(String memberId, Integer reviewId) {
        this.memberId = memberId;
        this.reviewId = reviewId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)  return true;
        if (!(o instanceof ReviewLikeId)) return false;
        ReviewLikeId that = (ReviewLikeId) o;
        return Objects.equals(memberId, that.memberId) && Objects.equals(reviewId, that.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, reviewId);
    }
}
