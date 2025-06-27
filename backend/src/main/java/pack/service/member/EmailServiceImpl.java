package pack.service.member;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pack.dto.member.EmailRequest;
import pack.dto.member.EmailVerifyRequest;
import pack.model.member.EmailCode;
import pack.repository.member.EmailCodeRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailCodeRepository emailCodeRepository;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    @Override
    public void sendCode(EmailRequest request) {
        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        EmailCode emailCode = emailCodeRepository.findByEmail(request.getEmail())
                .map(existing -> {
                    existing.setAuthCode(code);
                    existing.setVerified(false);
                    existing.setCreatedAt(LocalDateTime.now());
                    existing.setExpiredAt(LocalDateTime.now().plusMinutes(10));
                    return existing;
                })
                .orElse(EmailCode.builder()
                        .email(request.getEmail())
                        .authCode(code)
                        .isVerified(false)
                        .createdAt(LocalDateTime.now())
                        .expiredAt(LocalDateTime.now().plusMinutes(10))
                        .build());

        emailCodeRepository.save(emailCode);
        sendEmail(request.getEmail(), "[Replog] 이메일 인증 코드", "인증 코드: " + code);
    }

    @Override
    public boolean verifyCode(EmailVerifyRequest request) {
        return emailCodeRepository.findByEmail(request.getEmail())
                .filter(code -> code.getAuthCode().equals(request.getAuthCode()))
                .filter(code -> code.getExpiredAt().isAfter(LocalDateTime.now()))
                .map(code -> {
                    code.setVerified(true);
                    emailCodeRepository.save(code);
                    return true;
                }).orElse(false);
    }
} 
