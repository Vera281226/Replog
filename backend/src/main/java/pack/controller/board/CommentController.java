package pack.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pack.dto.board.CommentDto;
import pack.service.board.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/post/{postNo}")
    public List<CommentDto> getCommentsByPost(@PathVariable("postNo") Integer postNo) {
        return commentService.getCommentsByPostNo(postNo);
    }

    @GetMapping("/{commentNo}")
    public CommentDto getComment(@PathVariable("commentNo") Integer commentNo) {
        return commentService.getCommentById(commentNo);
    }

    @PostMapping
    public CommentDto createComment(@RequestBody CommentDto dto) {
        return commentService.createComment(dto);
    }

    @PutMapping("/{commentNo}")
    public CommentDto updateComment(@PathVariable("commentNo") Integer commentNo, @RequestBody CommentDto dto) {
        return commentService.updateComment(commentNo, dto);
    }

    @DeleteMapping("/{commentNo}")
    public void deleteComment(@PathVariable("commentNo") Integer commentNo) {
        commentService.deleteComment(commentNo);
    }
}