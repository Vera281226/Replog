package pack.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
	//인증번호 확인 요청
    private String email;
    private String authCode;
    private String newPassword;
}
