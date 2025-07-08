package pack.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pack.dto.common.ApiResponse;
import pack.dto.member.UserInfoResponse;
import pack.service.member.CustomUserDetails;
import pack.service.member.MemberService;
import pack.util.AuthUtil;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final MemberService memberService;

    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser() {

        String memberId = AuthUtil.getCurrentMemberId(); 
        UserInfoResponse userInfo = memberService.getUserInfo(memberId);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    
    // 로그아웃 엔드포인트는 SecurityConfig에서 자동 처리하므로 삭제(필요시 안내만)
    
    @GetMapping("/session-expired")
    public ResponseEntity<ApiResponse<Void>> sessionExpired() {
        return ResponseEntity.status(401)
            .body(ApiResponse.error("세션이 만료되었습니다. 다시 로그인하세요.", "SESSION_EXPIRED"));
    }
}
