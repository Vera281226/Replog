package pack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

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
    
    @Value("${app.profile.upload-dir:./uploads/profile/}")
    private String uploadDir;

    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*  /uploads/profile/** → 로컬 디렉터리 매핑 */
        registry
            .addResourceHandler("/uploads/profile/**")
            .addResourceLocations("file:" + uploadDir);
    }
}