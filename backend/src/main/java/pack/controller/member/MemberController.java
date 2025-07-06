package pack.controller.member;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.dto.common.ApiResponse;
import pack.dto.member.SignUpRequest;
import pack.service.member.MemberService;

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

    @PostMapping("/email-check")
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {
        if (memberService.emailExists(email)) {
            return ResponseEntity.ok("이미 사용 중인 이메일입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 이메일입니다.");
        }
    }
    
    @PostMapping("/nickname-check")
    public ResponseEntity<String> checkNickname(@RequestParam("nickname") String nickname) {
        if (memberService.nicknameExists(nickname)) {
            return ResponseEntity.ok("이미 사용 중인 닉네임입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 닉네임입니다.");
        }
    }
}
