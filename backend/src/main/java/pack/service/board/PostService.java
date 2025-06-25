package pack.service.board;

import pack.dto.board.PostDto;

import java.util.List;

import org.springframework.data.domain.Page;

public interface PostService {
    List<PostDto> getAllPosts();
    PostDto getPostById(Integer postNo);
    PostDto createPost(PostDto dto);
    PostDto updatePost(Integer postNo, PostDto dto);
    void deletePost(Integer postNo);
    PostDto getPostAndIncreaseViews(Integer postNo);
    Page<PostDto> getFilteredPosts(int page, String sortBy, String direction, String category, String searchType, String searchKeyword);
}