// AuthController.java
package pack.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.dto.common.ApiResponse;
import pack.dto.member.LoginRequest;
import pack.dto.member.UserInfoResponse;
import pack.service.member.MemberService;
import pack.util.SessionUtil;

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
            
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("loginMember", request.getMemberId());
            
            return ResponseEntity.ok(
                ApiResponse.success("로그인 성공", userInfo)
            );
        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(ApiResponse.error("아이디 또는 비밀번호가 올바르지 않습니다.", "LOGIN_FAILED"));
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
