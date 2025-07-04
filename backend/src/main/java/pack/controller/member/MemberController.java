package pack.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pack.dto.common.ApiResponse;
import pack.dto.member.SignUpRequest;
import pack.service.member.MemberService;

import java.util.List;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody SignUpRequest request, HttpServletRequest httpRequest) {
        try {
            memberService.signUp(request);

            // 자동 로그인 처리
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("loginMember", request.getMemberId());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            request.getMemberId(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER")));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok(ApiResponse.success("회원가입 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("회원가입 실패: " + e.getMessage()));
        }
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
