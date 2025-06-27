package pack.service.member;

import pack.dto.member.EmailRequest;
import pack.dto.member.EmailVerifyRequest;

public interface EmailService {
    void sendCode(EmailRequest request);
    boolean verifyCode(EmailVerifyRequest request);
    void sendEmail(String to, String subject, String body);
}