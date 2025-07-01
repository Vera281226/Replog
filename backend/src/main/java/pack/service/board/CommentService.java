package pack.service.board;

import pack.dto.board.CommentRequest;
import pack.dto.board.CommentResponse;

import java.util.List;

public interface CommentService {
	List<CommentResponse> getCommentsByPostNo(Integer postNo, String memberId);
    CommentResponse getCommentById(Integer commentNo);
    CommentResponse createComment(CommentRequest dto);
    CommentResponse updateComment(Integer commentNo, CommentRequest dto);
    void deleteComment(Integer commentNo);
    int countCommentsByMemberId(String memberId);
}