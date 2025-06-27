package pack.dto.member;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponse {

    private String id;
    private String nickname;
    private String email;
}
