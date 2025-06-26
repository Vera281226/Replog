package pack.service.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pack.model.board.Comment;
import pack.model.board.CommentLike;
import pack.repository.board.CommentLikeRepository;
import pack.repository.board.CommentRepository;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public boolean likeComment(String memberId, Integer commentNo) {
        Comment comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "댓글을 찾을 수 없습니다."));

        boolean exists = commentLikeRepository.existsByIdAndCommentNo(memberId, commentNo);
        if (exists) {
            commentLikeRepository.deleteByIdAndCommentNo(memberId, commentNo);
            comment.decreaseLikes(); // 좋아요 수 감소
        } else {
            commentLikeRepository.save(new CommentLike(memberId, commentNo, null));
            comment.increaseLikes(); // 좋아요 수 증가
        }

        commentRepository.save(comment); // 변경 감지 가능하지만 명시적으로 저장
        return !exists;
    }

    @Override
    public boolean isLiked(String memberId, Integer commentNo) {
        return commentLikeRepository.existsByIdAndCommentNo(memberId, commentNo);
    }
}
