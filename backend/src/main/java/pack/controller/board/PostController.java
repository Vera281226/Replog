package pack.controller.board;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pack.dto.board.PostDto;
import pack.service.board.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 전체 출력 (페이징 x)
    @GetMapping
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }
    
    // 조회 + 조회수 증가
    @GetMapping("/{postNo}")
    public PostDto getPostDetail(@PathVariable("postNo") Integer postNo) {
        return postService.getPostAndIncreaseViews(postNo);
    }
    
    // 글쓰기
    @PostMapping
    public PostDto createPost(@RequestBody PostDto dto) {
        return postService.createPost(dto);
    }

    // 수정
    @PutMapping("/{postNo}")
    public PostDto updatePost(@PathVariable("postNo") Integer postNo, @RequestBody PostDto dto) {
        return postService.updatePost(postNo, dto);
    }

    // 삭제
    @DeleteMapping("/{postNo}")
    public void deletePost(@PathVariable("postNo") Integer postNo) {
        postService.deletePost(postNo);
    }
    // 페이징 + 정렬 + 검색
    @GetMapping("/filter")
    public Page<PostDto> getFilteredAndSearchedPosts(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "direction", defaultValue = "desc") String direction,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "searchType", required = false) String searchType,
            @RequestParam(name = "searchKeyword", required = false) String searchKeyword
    ) {
        return postService.getFilteredPosts(page, sortBy, direction, category, searchType, searchKeyword);
    }
}