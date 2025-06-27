package pack.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {

    private String email;
    private String authCode;
    private String newPassword;
}
