package pack.service.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.dto.board.PostRequest;
import pack.dto.board.PostResponse;
import pack.model.board.Post;
import pack.repository.board.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    // 전체 조회
    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 단순조회(조회수 증가X)
    @Override
    public PostResponse getPostById(Integer postNo) {
        Post post = postRepository.findById(postNo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "게시글을 찾을 수 없습니다."));
        return toDto(post);
    }

    // 글쓰기
    @Override
    public PostResponse createPost(PostRequest dto) {
        validatePostRequest(dto);
        Post post = toEntity(dto);
        return toDto(postRepository.save(post));
    }

    // 수정
    @Override
    public PostResponse updatePost(Integer postNo, PostRequest dto) {
        Post post = postRepository.findById(postNo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "수정할 게시글이 존재하지 않습니다."));

        validatePostRequest(dto);

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCategory(dto.getCategory());
        post.setUpdatedAt(LocalDateTime.now());
        return toDto(postRepository.save(post));
    }

    // 삭제
    @Override
    public void deletePost(Integer postNo) {
        if (!postRepository.existsById(postNo)) {
            throw new ResponseStatusException(NOT_FOUND, "삭제할 게시글이 존재하지 않습니다.");
        }
        postRepository.deleteById(postNo);
    }

    // 조회 + 조회수 증가
    @Override
    public PostResponse getPostAndIncreaseViews(Integer postNo) {
        Post post = postRepository.findById(postNo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "게시글을 찾을 수 없습니다."));
        post.setViews(post.getViews() + 1);
        Post updated = postRepository.save(post);
        return toDto(updated);
    }

    // 정렬 + 페이징 + 검색
    @Override
    public Page<PostResponse> getFilteredPosts(int page, String sortBy, String direction, String category, String searchType, String searchKeyword) {
        if (!List.of("createdAt", "views", "likes").contains(sortBy)) {
            throw new ResponseStatusException(BAD_REQUEST, "허용되지 않은 정렬 기준입니다.");
        }

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
                    default -> throw new ResponseStatusException(BAD_REQUEST, "지원하지 않는 검색 조건입니다.");
                }
            } else {
                switch (searchType) {
                    case "title" -> posts = postRepository.findByTitleContaining(searchKeyword, pageable);
                    case "content" -> posts = postRepository.findByContentContaining(searchKeyword, pageable);
                    case "nickname" -> posts = postRepository.findByNicknameContaining(searchKeyword, pageable);
                    default -> throw new ResponseStatusException(BAD_REQUEST, "지원하지 않는 검색 조건입니다.");
                }
            }
        } else if (hasCategory) {
            posts = postRepository.findByCategory(category, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }

        return posts.map(this::toDto);
    }

    // Entity -> DTO 변환
    private PostResponse toDto(Post post) {
        return PostResponse.builder()
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

    // DTO -> Entity 변환 (등록 시)
    private Post toEntity(PostRequest dto) {
        return Post.builder()
                .id(dto.getId())
                .nickname(dto.getNickname())
                .category(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .isHidden(false)
                .views(0)
                .likes(0)
                .build();
    }

    // 유효성 검사
    private void validatePostRequest(PostRequest dto) {
        if (dto.getId() == null || dto.getId().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "작성자 ID는 필수입니다.");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "제목은 필수입니다.");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "내용은 필수입니다.");
        }
    }
}
