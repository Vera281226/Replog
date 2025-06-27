package pack.controller.member;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.service.member.EmailService;
import pack.service.member.MemberService;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final EmailService emailService;
    private final MemberService memberService;

    @PostMapping("/reset")
    public ResponseEntity<String> sendResetEmail(@RequestParam String email) throws MessagingException {
        if (!memberService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("이메일이 존재하지 않습니다.");
        }

        String tempPassword = memberService.resetPassword(email);
        emailService.sendEmail(email, "비밀번호 재설정", "임시 비밀번호: " + tempPassword);
        return ResponseEntity.ok("임시 비밀번호가 이메일로 발송되었습니다.");
    }
}
