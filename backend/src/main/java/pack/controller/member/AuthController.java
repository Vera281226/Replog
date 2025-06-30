package pack.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.dto.member.LoginRequest;
import pack.service.member.MemberService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        boolean isAuthenticated = memberService.validateLogin(request.getMemberId(), request.getPwd());

        if (isAuthenticated) {
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("loginMember", request.getMemberId());
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
