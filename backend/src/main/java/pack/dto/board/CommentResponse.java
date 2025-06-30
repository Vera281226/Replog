package pack.dto.board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Integer commentNo;
    private String memberId;
    private String nickname;
    private Integer postNo;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long likes;
    private Boolean isLiked;
}