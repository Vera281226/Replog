package pack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로에 대해
                .allowedOrigins("http://localhost:3000") // React 개발 서버 주소 허용
                .allowedMethods("*") // GET, POST 등 모든 메서드 허용
                .allowedHeaders("*")
                .allowCredentials(true); // 필요 시 쿠키 허용
    }
}