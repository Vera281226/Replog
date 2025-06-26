package pack.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pack.service.board.CommentLikeService;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    // 좋아요 토글
    @PostMapping("/{commentNo}/like")
    public String toggleLike(@PathVariable("commentNo") Integer commentNo, @RequestParam("id") String id) {
        boolean liked = commentLikeService.likeComment(id, commentNo);
        return liked ? "liked" : "unliked";
    }

    // 좋아요 확인
    @GetMapping("/{commentNo}/like")
    public boolean checkLiked(@PathVariable("commentNo") Integer commentNo, @RequestParam("id") String id) {
        return commentLikeService.isLiked(id, commentNo);
    }
}