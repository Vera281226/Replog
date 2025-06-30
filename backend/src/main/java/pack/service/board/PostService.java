package pack.service.board;

import pack.dto.board.PostRequest;
import pack.dto.board.PostResponse;

import org.springframework.data.domain.Page;

public interface PostService {
    PostResponse getPostById(Integer postNo);
    PostResponse createPost(PostRequest dto);
    PostResponse updatePost(Integer postNo, PostRequest dto);
    void deletePost(Integer postNo);
    PostResponse getPostAndIncreaseViews(Integer postNo);
    Page<PostResponse> getFilteredPosts(int page, String sortBy, String direction, String category, String searchType, String searchKeyword);
}