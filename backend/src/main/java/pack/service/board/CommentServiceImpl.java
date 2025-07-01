package pack.service.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.dto.board.CommentRequest;
import pack.dto.board.CommentResponse;
import pack.model.board.Comment;
import pack.repository.board.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;

    // Entity → Response DTO (memberId 기반 좋아요 여부 포함)
    private CommentResponse toDto(Comment entity, String memberId) {
        long likeCount = commentLikeService.getLikeCount(entity.getCommentNo());
        boolean isLiked = (memberId != null && !memberId.isBlank())
                && commentLikeService.isCommentLiked(memberId, entity.getCommentNo());

        return CommentResponse.builder()
                .commentNo(entity.getCommentNo())
                .memberId(entity.getMemberId())
                .nickname(entity.getNickname())
                .postNo(entity.getPostNo())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .likes(likeCount)
                .isLiked(isLiked)
                .build();
    }

    // Request DTO → Entity
    private Comment toEntity(CommentRequest dto) {
        return Comment.builder()
                .memberId(dto.getMemberId())
                .nickname(dto.getNickname())
                .postNo(dto.getPostNo())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void validateCommentRequest(CommentRequest dto) {
        if (dto.getMemberId() == null || dto.getMemberId().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "작성자 ID는 필수입니다.");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "댓글 내용은 필수입니다.");
        }
    }

    @Override
    public List<CommentResponse> getCommentsByPostNo(Integer postNo, String memberId) {
        return commentRepository.findByPostNoOrderByCreatedAtAsc(postNo)
                .stream()
                .map(comment -> toDto(comment, memberId))
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponse getCommentById(Integer commentNo) {
        Comment comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "댓글을 찾을 수 없습니다."));
        return toDto(comment, null); // 단일 조회에는 isLiked 불필요하거나 비로그인 처리
    }

    @Override
    public CommentResponse createComment(CommentRequest dto) {
        validateCommentRequest(dto);
        Comment comment = toEntity(dto);
        return toDto(commentRepository.save(comment), dto.getMemberId());
    }

    @Override
    public CommentResponse updateComment(Integer commentNo, CommentRequest dto) {
        Comment comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "수정할 댓글이 존재하지 않습니다."));

        validateCommentRequest(dto);
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        return toDto(commentRepository.save(comment), dto.getMemberId());
    }

    @Override
    public void deleteComment(Integer commentNo) {
        if (!commentRepository.existsById(commentNo)) {
            throw new ResponseStatusException(NOT_FOUND, "삭제할 댓글이 존재하지 않습니다.");
        }
        commentRepository.deleteById(commentNo);
    }
    
    @Override
    public int countCommentsByMemberId(String memberId) {
        return commentRepository.countByMemberId(memberId);
    }
}