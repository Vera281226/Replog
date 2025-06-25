package pack.service.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pack.dto.board.CommentDto;
import pack.model.board.Comment;
import pack.repository.board.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private CommentDto toDto(Comment entity) {
        return CommentDto.builder()
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

    private Comment toEntity(CommentDto dto) {
        return Comment.builder()
                .commentNo(dto.getCommentNo())
                .id(dto.getId())
                .nickname(dto.getNickname())
                .postNo(dto.getPostNo())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .isHidden(dto.getIsHidden())
                .likes(dto.getLikes())
                .build();
    }

    @Override
    public List<CommentDto> getCommentsByPostNo(Integer postNo) {
        return commentRepository.findByPostNoAndIsHiddenFalseOrderByCreatedAtAsc(postNo)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Integer commentNo) {
        return commentRepository.findById(commentNo)
                .map(this::toDto).orElse(null);
    }

    @Override
    public CommentDto createComment(CommentDto dto) {
        Comment comment = toEntity(dto);
        comment.setCreatedAt(LocalDateTime.now());
        return toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Integer commentNo, CommentDto dto) {
        Comment comment = commentRepository.findById(commentNo).orElseThrow();
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setIsHidden(dto.getIsHidden());
        return toDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Integer commentNo) {
        commentRepository.deleteById(commentNo);
    }
}