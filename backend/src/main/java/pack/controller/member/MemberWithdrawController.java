package pack.controller.member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pack.dto.member.MemberWithdrawRequest;
import pack.service.member.MemberService;
import pack.util.AuthUtil;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberWithdrawController {

    private final MemberService memberService;

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody MemberWithdrawRequest request, HttpSession session) {
        String memberId = AuthUtil.getCurrentMemberId();

        if (memberId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        boolean success = memberService.withdraw(memberId, request.getPwd());

        if (success) {
            session.invalidate(); // 세션 제거 (자동 로그아웃)
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
    }
}
