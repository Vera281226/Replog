package pack.dto.board;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeDto {
    private String memberId;
    private Integer postNo;
    private LocalDateTime likedAt;
}