package pack.service.member;

import pack.dto.member.EmailAuthRequest;
import pack.dto.member.EmailVerifyRequest;
import pack.dto.member.PasswordResetRequest;

public interface EmailService {

	void sendAuthCode(EmailAuthRequest dto);
	boolean verifyAuthCode(EmailVerifyRequest dto);
	boolean resetPassword(PasswordResetRequest dto);
}
