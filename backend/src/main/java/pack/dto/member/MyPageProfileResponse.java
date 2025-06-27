package pack.dto.member;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageProfileResponse {

    private String id;
    private String name;
    private String nickname;
    private String introduction;
    private String profileImagePath;
    private List<String> genres;
}