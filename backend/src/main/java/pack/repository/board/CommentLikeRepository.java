package pack.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.board.CommentLike;
import pack.model.board.CommentLikeId;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    boolean existsByIdAndCommentNo(String id, Integer commentNo);
    void deleteByIdAndCommentNo(String id, Integer commentNo);
}