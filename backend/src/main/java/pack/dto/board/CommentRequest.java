package pack.dto.board;

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
public class CommentRequest {
    private String id;          // 작성자 ID
    private String nickname;    // 작성자 닉네임
    private Integer postNo;     // 대상 게시글 번호
    private String content;     // 댓글 내용
}