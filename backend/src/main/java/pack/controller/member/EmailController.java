package pack.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.dto.member.EmailRequest;
import pack.dto.member.EmailVerifyRequest;
import pack.service.member.EmailService;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendCode(@RequestBody EmailRequest request) {
        emailService.sendCode(request);
        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody EmailVerifyRequest request) {
        boolean result = emailService.verifyCode(request);
        return result
                ? ResponseEntity.ok("인증에 성공했습니다.")
                : ResponseEntity.badRequest().body("인증에 실패했습니다.");
    }
}
