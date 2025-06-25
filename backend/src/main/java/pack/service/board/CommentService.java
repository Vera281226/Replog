package pack.service.board;

import pack.dto.board.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByPostNo(Integer postNo);
    CommentDto getCommentById(Integer commentNo);
    CommentDto createComment(CommentDto dto);
    CommentDto updateComment(Integer commentNo, CommentDto dto);
    void deleteComment(Integer commentNo);
}