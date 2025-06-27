package pack.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.dto.member.*;
import pack.service.member.MemberService;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberRequest request) {
        memberService.registerMember(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
    
    @GetMapping("/check-email")
    public boolean checkEmailDuplicate(@RequestParam String email) {
        return memberService.existsByEmail(email);
    }
   
    @PutMapping("/update")
    public ResponseEntity<String> updateMember(@RequestBody MemberRequest memberRequest) {
        memberService.updateMember(memberRequest);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request) {
        memberService.changePassword(request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    @PostMapping("/send-reset-code")
    public ResponseEntity<String> sendResetCode(@RequestParam String email) {
        memberService.sendPasswordResetCode(email);
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        memberService.resetPassword(request);
        return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody WithdrawRequest request) {
        memberService.withdraw(request);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
    
    @PostMapping("/update-genres")
    public ResponseEntity<Void> updateGenres(@RequestBody GenreUpdateRequest request) {
        memberService.updateGenres(request);
        return ResponseEntity.ok().build();
    }
    
    
}