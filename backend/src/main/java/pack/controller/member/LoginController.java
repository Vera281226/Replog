package pack.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import pack.dto.member.LoginRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword());
            Authentication auth = authenticationManager.authenticate(token);

            // 세션 기반 SecurityContext 저장
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            return "로그인 성공";
        } catch (AuthenticationException e) {
            return "로그인 실패: " + e.getMessage();
        }
    }
}
