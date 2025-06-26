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

    // Entity → Response DTO
    private CommentResponse toDto(Comment entity) {
        return CommentResponse.builder()
                .commentNo(entity.getCommentNo())
                .id(entity.getId())
                .nickname(entity.getNickname())
                .postNo(entity.getPostNo())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isHidden(entity.getIsHidden())
                .likes(entity.getLikes())
                .build();
    }

    // Request DTO → Entity
    private Comment toEntity(CommentRequest dto) {
        return Comment.builder()
                .id(dto.getId())
                .nickname(dto.getNickname())
                .postNo(dto.getPostNo())
                .content(dto.getContent())
                .isHidden(false)
                .likes(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void validateCommentRequest(CommentRequest dto) {
        if (dto.getId() == null || dto.getId().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "작성자 ID는 필수입니다.");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "댓글 내용은 필수입니다.");
        }
    }

    @Override
    public List<CommentResponse> getCommentsByPostNo(Integer postNo) {
        return commentRepository.findByPostNoAndIsHiddenFalseOrderByCreatedAtAsc(postNo)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public CommentResponse getCommentById(Integer commentNo) {
        Comment comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "댓글을 찾을 수 없습니다."));
        return toDto(comment);
    }

    @Override
    public CommentResponse createComment(CommentRequest dto) {
        validateCommentRequest(dto);
        Comment comment = toEntity(dto);
        return toDto(commentRepository.save(comment));
    }

    @Override
    public CommentResponse updateComment(Integer commentNo, CommentRequest dto) {
        Comment comment = commentRepository.findById(commentNo)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "수정할 댓글이 존재하지 않습니다."));

        validateCommentRequest(dto);

        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Integer commentNo) {
        if (!commentRepository.existsById(commentNo)) {
            throw new ResponseStatusException(NOT_FOUND, "삭제할 댓글이 존재하지 않습니다.");
        }
        commentRepository.deleteById(commentNo);
    }
}
