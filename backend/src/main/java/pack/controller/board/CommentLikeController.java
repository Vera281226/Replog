package pack.controller.board;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pack.service.board.CommentLikeService;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    // 좋아요 토글
    @PostMapping("/{commentNo}/like")
    public String toggleLike(@PathVariable("commentNo") Integer commentNo, @RequestParam("memberId") String memberId) {
        boolean liked = commentLikeService.likeComment(memberId, commentNo);
        return liked ? "liked" : "unliked";
    }
    
    // 좋아요 확인
    @GetMapping("/{commentNo}/like")
    public boolean checkLiked(@PathVariable("commentNo") Integer commentNo, @RequestParam("memberId") String memberId) {
        return commentLikeService.isCommentLiked(memberId, commentNo);
    }
}