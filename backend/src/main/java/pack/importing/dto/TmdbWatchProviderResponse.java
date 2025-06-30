package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * TMDB 플랫폼 제공 정보 전체 응답 DTO
 * <p>
 * /movie/{id}/watch/providers API 응답 구조를 매핑합니다.
 * <br>
 * 예시 응답: { "id": 12345, "results": { "KR": { ... } } }
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbWatchProviderResponse {

    /**
     * TMDB 콘텐츠 ID
     */
    private int id;

    /**
     * 국가별 플랫폼 정보
     * <p>
     * 예: "KR" → TmdbWatchProviderRegion
     */
    private Map<String, TmdbWatchProviderRegion> results;
}
