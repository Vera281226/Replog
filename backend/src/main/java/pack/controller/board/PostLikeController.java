package pack.controller.board;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
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
}