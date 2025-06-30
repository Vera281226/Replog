package pack.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.board.CommentLike;
import pack.model.board.CommentLikeId;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    boolean existsByMemberIdAndCommentNo(String memberId, Integer commentNo);
    void deleteByMemberIdAndCommentNo(String memberId, Integer commentNo);
    long countByCommentNo(Integer commentNo); // 좋아요 수 카운트용 추가
}