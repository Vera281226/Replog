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

        boolean exists = commentLikeRepository.existsByMemberIdAndCommentNo(memberId, commentNo);
        if (exists) {
            commentLikeRepository.deleteByMemberIdAndCommentNo(memberId, commentNo);
        } else {
            commentLikeRepository.save(new CommentLike(memberId, commentNo, null));
        }

        return !exists;
    }
    
    @Override
    public boolean isCommentLiked(String memberId, Integer commentNo) {
        return commentLikeRepository.existsByMemberIdAndCommentNo(memberId, commentNo);
    }

    @Override
    public long getLikeCount(Integer commentNo) {
        return commentLikeRepository.countByCommentNo(commentNo);
    }
}