// AuthController.java
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
import pack.dto.member.LoginRequest;
import pack.dto.member.UserInfoResponse;
import pack.service.member.MemberService;
import pack.util.SessionUtil;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserInfoResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        try {
            UserInfoResponse userInfo = memberService.authenticateUser(request.getMemberId(), request.getPwd());

            // 기존 세션 저장
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("loginUser", userInfo);

            // Spring Security 인증 정보 수동 등록 (중요)
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userInfo.getMemberId(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER")));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("로그인 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser(HttpSession session) {
        String memberId = SessionUtil.getLoginMemberId(session);
        
        if (memberId == null) {
            return ResponseEntity.status(401)
                .body(ApiResponse.error("로그인이 필요합니다.", "AUTH_REQUIRED"));
        }
        
        try {
            UserInfoResponse userInfo = memberService.getUserInfo(memberId);
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("사용자 정보 조회 실패", "USER_INFO_ERROR"));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다.", null));
    }
}
