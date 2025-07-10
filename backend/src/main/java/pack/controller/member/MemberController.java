package pack.controller.member;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import pack.dto.common.ApiResponse;
import pack.dto.member.MemberInfoResponse;
import pack.dto.member.SignUpRequest;
import pack.service.member.MemberService;
import pack.util.AuthUtil;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody SignUpRequest request) {
        memberService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입 완료"));
    }

    @PostMapping("/id-check")
    public ResponseEntity<String> checkMemberId(@RequestBody Map<String, String> body) {
        String memberId = body.get("memberId");
        if (memberService.memberIdExists(memberId)) {
            return ResponseEntity.ok("이미 사용 중인 아이디입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 아이디입니다.");
        }
    }

    @PostMapping("/nickname-check")
    public ResponseEntity<String> checkNickname(@RequestBody Map<String, String> body) {
        String nickname = body.get("nickname");
        if (memberService.nicknameExists(nickname)) {
            return ResponseEntity.ok("이미 사용 중인 닉네임입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 닉네임입니다.");
        }
    }

    @PostMapping("/email-check")
    public ResponseEntity<String> checkEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (memberService.emailExists(email)) {
            return ResponseEntity.ok("이미 사용 중인 이메일입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 이메일입니다.");
        }
    }
    
    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo() {
        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }
        MemberInfoResponse info = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(info);
    }
}
