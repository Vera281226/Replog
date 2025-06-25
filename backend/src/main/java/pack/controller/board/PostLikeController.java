package pack.controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pack.service.board.PostLikeService;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;
    // 좋아요 토글
    @PostMapping("/{postNo}/like")
    public String toggleLike(@PathVariable("postNo") Integer postNo, @RequestParam("id") String id) {
        boolean liked = postLikeService.likePost(id, postNo);
        return liked ? "liked" : "unliked";
    }
    // 좋아요 확인
    @GetMapping("/{postNo}/like")
    public boolean checkLiked(@PathVariable("postNo") Integer postNo, @RequestParam("id") String id) {
        return postLikeService.isPostLiked(id, postNo);
    }
    // 토글이라 삭제 확인용으로 만들었음
    @DeleteMapping("/{postNo}/like")
    public String cancelLike(@PathVariable("postNo") Integer postNo, @RequestParam("id") String id) {
        postLikeService.cancelLike(id, postNo);
        return "unliked";
    }
}