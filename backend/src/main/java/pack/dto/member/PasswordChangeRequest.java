package pack.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
	// 비밀번호 변경 요청
	private String memberId;  
    private String currentPassword;
    private String newPassword;
}
