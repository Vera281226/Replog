package pack.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import pack.service.member.CustomUserDetailsService;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthEntryPoint customAuthEntryPoint;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        http
            // 1. CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 2. CSRF 설정
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()
                		)
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers(
                        "/", 
                        "/api/auth/login", 
                        "/api/member/signup", 
                        "/api/auth/logout"
                    )
            )


            // 4. 예외 처리 (401/403 핸들러)
            .exceptionHandling(eh -> eh
                .authenticationEntryPoint(customAuthEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            )

            // 5. 인가(Authorization) 정책
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/current-user",
                    "/api/member/signup",
                    "/api/auth/logout",
                    "/api/email/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/chat/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/theaters/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/partyposts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/member/profile/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reports/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/posts/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasRole("USER")
                .requestMatchers(HttpMethod.PATCH, "/api/posts/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/comments/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/comments/**").hasRole("USER")
                .requestMatchers(HttpMethod.PATCH, "/api/comments/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/reviews/**").hasRole("USER")
                .requestMatchers(HttpMethod.PATCH, "/api/reviews/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/chat/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/chat/**").hasRole("USER")
                .requestMatchers(HttpMethod.PATCH, "/api/chat/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/chat/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/partyposts/**").hasRole("USER")
                .requestMatchers(HttpMethod.PUT, "/api/partyposts/**").hasRole("USER")
                .requestMatchers(HttpMethod.PATCH, "/api/partyposts/**").hasRole("USER")
                .requestMatchers(HttpMethod.DELETE, "/api/partyposts/**").hasRole("USER")
                .requestMatchers("/api/member/**").hasRole("USER")
                .requestMatchers("/api/reports/**").hasRole("USER")
                .requestMatchers("/api/admin/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
            	    .loginProcessingUrl("/api/auth/login")
            	    .usernameParameter("memberId")
            	    .passwordParameter("password")
            	    .successHandler((request, response, authentication) -> {
            	        // 세션 강제 생성 (없으면 생성)
            	        request.getSession(true);

            	        // CSRF 토큰을 강제로 생성 및 쿠키로 내려주기
            	        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            	        if (csrfToken != null) {
            	            // Spring Security가 CookieCsrfTokenRepository로 자동 쿠키 발급
            	            // 필요시 response 헤더로도 내려줄 수 있음 (SPA에서 직접 읽으려면)
            	            response.setHeader("X-CSRF-TOKEN", csrfToken.getToken());
            	        }

            	        response.setStatus(HttpServletResponse.SC_OK);
            	        response.setContentType("application/json;charset=UTF-8");
            	        response.getWriter().write("{\"success\":true,\"message\":\"로그인 성공\"}");
            	    })
            	    .failureHandler((request, response, exception) -> {
            	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	        response.setContentType("application/json;charset=UTF-8");
            	        response.getWriter().write("{\"success\":false,\"message\":\"로그인 실패: " + exception.getMessage() + "\"}");
            	    })
            	    .permitAll()
            	)
            	.logout(logout -> logout
            	    .logoutUrl("/api/auth/logout")
            	    .invalidateHttpSession(true)
            	    .clearAuthentication(true)
            	    .deleteCookies("JSESSIONID", "XSRF-TOKEN")
            	    .logoutSuccessHandler((request, response, authentication) -> {
            	        response.setStatus(HttpServletResponse.SC_OK);
            	        response.setContentType("application/json;charset=UTF-8");
            	        response.getWriter().write("{\"success\":true,\"message\":\"로그아웃 되었습니다.\"}");
            	    })
            	)
            	.sessionManagement(session -> session
            	    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            	    .invalidSessionUrl("/api/auth/session-expired")
            	    .sessionFixation().changeSessionId()
            	    .maximumSessions(1)
            	);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }
}
