package pack.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pack.dto.member.EmailAuthRequest;
import pack.dto.member.EmailVerifyRequest;
import pack.dto.member.PasswordResetRequest;
import pack.service.member.EmailService;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

	private final EmailService emailService;
	
	// 인증 코드 발송
	@PostMapping("/send")
	public ResponseEntity<String> sendAuthCode(@RequestBody EmailAuthRequest dto){
		emailService.sendAuthCode(dto);
		return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
	}
	
	// 인증 코드 검증하고 성공 여부에 따라 메세지 반환
	@PostMapping("/verify")
	public ResponseEntity<String> verifyAuthCode(@RequestBody EmailVerifyRequest dto){
		boolean isVerified = emailService.verifyAuthCode(dto);
		if(isVerified) {
			return ResponseEntity.ok("인증이 완료되었습니다.");
		} else {
			return ResponseEntity.badRequest().body("인증에 실패했습니다. 인증 코드가 올바르지 않거나 만료되었습니다.");
		}
	}
	
	// 비밀번호 재설정
	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest dto){
		boolean result = emailService.resetPassword(dto);
		if(result) {
			return ResponseEntity.ok("비밀번호가 변경되었습니다.");
		} else {
			return ResponseEntity.badRequest().body("비밀번호 변경에 실패했습니다.");
		}
	}
}
