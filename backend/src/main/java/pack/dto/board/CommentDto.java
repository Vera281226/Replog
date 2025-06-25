package pack.dto.board;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Integer commentNo;
    private String id;
    private String nickname;
    private Integer postNo;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isHidden;
    private Integer likes;
}