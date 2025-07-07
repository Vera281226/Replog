package pack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * TmdbProperties
 *
 * ✅ TMDB API 관련 설정 정보를 외부 설정 파일로부터 주입받는 클래스입니다.
 * - 설정 파일: application-secret.properties 또는 application.yml
 * - 사용 예: tmdb.apiKey, tmdb.token, tmdb.baseUrl
 *
 * ✅ 설정 방식:
 * - @Value("...") 방식이 아닌 @ConfigurationProperties(prefix = "tmdb") 사용
 * - 설정 키가 많아질 경우 유지보수와 테스트에 유리
 */
@Getter
@Setter
@Configuration // 이 클래스를 Spring Bean으로 등록
@ConfigurationProperties(prefix = "tmdb") // tmdb.apiKey / tmdb.token / tmdb.baseUrl 등 자동 매핑
public class TmdbProperties {

    /** TMDB API Key (쿼리 파라미터용) */
    private String apiKey;

    /** TMDB Bearer Token (Authorization 헤더용) */
    private String token;

    /** TMDB API Base URL (예: https://api.themoviedb.org/3) */
    private String baseUrl;
}
