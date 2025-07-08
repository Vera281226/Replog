// UserInfoResponse.java
package pack.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
    private String memberId;
    private String nickname;
    private String email;
    private String role;
}
