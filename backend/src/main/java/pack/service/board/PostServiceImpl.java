package pack.service.board;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pack.dto.board.PostDto;
import pack.model.board.Post;
import pack.repository.board.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    // 단순조회(조회수 증가X)
    @Override
    public PostDto getPostById(Integer postNo) {
        return postRepository.findById(postNo)
                .map(this::toDto)
                .orElse(null);
    }
    // 글쓰기
    @Override
    public PostDto createPost(PostDto dto) {
        Post post = toEntity(dto);
        return toDto(postRepository.save(post));
    }
    // 수정
    @Override
    public PostDto updatePost(Integer postNo, PostDto dto) {
        Post post = postRepository.findById(postNo).orElseThrow();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCategory(dto.getCategory());
        post.setUpdatedAt(LocalDateTime.now());
        return toDto(postRepository.save(post));
    }
    // 삭제
    @Override
    public void deletePost(Integer postNo) {
        postRepository.deleteById(postNo);
    }
    // 조회 + 조회수 증가
    @Override
    public PostDto getPostAndIncreaseViews(Integer postNo) {
        Post post = postRepository.findById(postNo).orElseThrow();

        post.setViews(post.getViews() + 1); // 조회수 1 증가
        Post updated = postRepository.save(post); // 저장

        return toDto(updated);
    }
    // 정렬 + 페이징 + 검색
    @Override
    public Page<PostDto> getFilteredPosts(int page, String sortBy, String direction, String category, String searchType, String searchKeyword) {
        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, 20, sort);
        Page<Post> posts;

        boolean hasCategory = category != null && !category.isEmpty();
        boolean hasSearch = searchType != null && searchKeyword != null && !searchKeyword.isEmpty();

        if (hasSearch) {
            if (hasCategory) {
                switch (searchType) {
                    case "title" -> posts = postRepository.findByTitleContainingAndCategory(searchKeyword, category, pageable);
                    case "content" -> posts = postRepository.findByContentContainingAndCategory(searchKeyword, category, pageable);
                    case "nickname" -> posts = postRepository.findByNicknameContainingAndCategory(searchKeyword, category, pageable);
                    default -> posts = postRepository.findAll(pageable);
                }
            } else {
                switch (searchType) {
                    case "title" -> posts = postRepository.findByTitleContaining(searchKeyword, pageable);
                    case "content" -> posts = postRepository.findByContentContaining(searchKeyword, pageable);
                    case "nickname" -> posts = postRepository.findByNicknameContaining(searchKeyword, pageable);
                    default -> posts = postRepository.findAll(pageable);
                }
            }
        } else if (hasCategory) {
            posts = postRepository.findByCategory(category, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }

        return posts.map(this::toDto);
    }

    private PostDto toDto(Post post) {
        return PostDto.builder()
                .postNo(post.getPostNo())
                .id(post.getId())
                .nickname(post.getNickname())
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .isHidden(post.getIsHidden())
                .views(post.getViews())
                .likes(post.getLikes())
                .build();
    }

    private Post toEntity(PostDto dto) {
        return Post.builder()
                .postNo(dto.getPostNo())
                .id(dto.getId())
                .nickname(dto.getNickname())
                .category(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .isHidden(dto.getIsHidden() != null ? dto.getIsHidden() : false)
                .views(dto.getViews() != null ? dto.getViews() : 0)
                .likes(dto.getLikes() != null ? dto.getLikes() : 0)
                .build();
    }
}