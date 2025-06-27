package pack.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoResponse {

    private String id;
    private String name;
    private String nickname;
    private String email;
    private String role;
}
