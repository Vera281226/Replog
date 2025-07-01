package pack.controller.board;

import lombok.RequiredArgsConstructor;

import pack.dto.board.CommentRequest;
import pack.dto.board.CommentResponse;
import pack.service.board.CommentService; 

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/post/{postNo}")
    public List<CommentResponse> getCommentsByPost(@PathVariable("postNo") Integer postNo,
                                                   @RequestParam(value = "memberId", required = false) String memberId) {
        return commentService.getCommentsByPostNo(postNo, memberId);
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
    
    // 내가 쓴 댓글 수 조회
    @GetMapping("/count")
    public int getMyCommentCount(@RequestParam("memberId") String memberId) {
        return commentService.countCommentsByMemberId(memberId);
    }
}