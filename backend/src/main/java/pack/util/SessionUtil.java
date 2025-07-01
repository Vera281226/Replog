// SessionUtil.java
package pack.util;

import jakarta.servlet.http.HttpSession;

public final class SessionUtil {
    
    private static final String LOGIN_MEMBER_KEY = "loginMember";
    
    private SessionUtil() {
        // 유틸리티 클래스 - 인스턴스 생성 방지
    }
    
    public static String getLoginMemberId(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(LOGIN_MEMBER_KEY);
    }
    
    public static boolean isLoggedIn(HttpSession session) {
        return getLoginMemberId(session) != null;
    }
    
    public static void requireLogin(HttpSession session) {
        if (!isLoggedIn(session)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
    }
    
    public static void setLoginMember(HttpSession session, String memberId) {
        if (session != null && memberId != null) {
            session.setAttribute(LOGIN_MEMBER_KEY, memberId);
        }
    }
}
