package pack.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import pack.dto.board.CommentRequest;
import pack.dto.board.CommentResponse;
import pack.service.board.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/post/{postNo}")
    public List<CommentResponse> getCommentsByPost(@PathVariable("postNo") Integer postNo) {
        return commentService.getCommentsByPostNo(postNo);
    }

    @GetMapping("/{commentNo}")
    public CommentResponse getComment(@PathVariable("commentNo") Integer commentNo) {
        return commentService.getCommentById(commentNo);
    }

    @PostMapping
    public CommentResponse createComment(@RequestBody CommentRequest dto) {
        return commentService.createComment(dto);
    }

    @PutMapping("/{commentNo}")
    public CommentResponse updateComment(@PathVariable("commentNo") Integer commentNo, @RequestBody CommentRequest dto) {
        return commentService.updateComment(commentNo, dto);
    }

    @DeleteMapping("/{commentNo}")
    public void deleteComment(@PathVariable("commentNo") Integer commentNo) {
        commentService.deleteComment(commentNo);
    }
}