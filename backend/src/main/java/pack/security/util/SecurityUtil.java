package pack.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증된 사용자가 없습니다.");
        }

        return authentication.getName(); // CustomUserDetails에서 memberId가 username으로 설정됨
    }
}
