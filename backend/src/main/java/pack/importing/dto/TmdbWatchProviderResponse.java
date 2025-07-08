package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * TmdbWatchProviderResponse
 * -------------------------------------------------------------
 * ✅ TMDB의 국가별 OTT 플랫폼 응답을 매핑하는 DTO 클래스입니다.
 *
 * ✅ 사용 API: /movie/{id}/watch/providers
 * ✅ "results"는 국가 코드(KR, US 등)를 키로 갖는 Map 구조
 * ✅ 각 항목은 TmdbWatchProviderRegion으로 매핑됨
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbWatchProviderResponse {
    private Map<String, TmdbWatchProviderRegion> results = new HashMap<>();  // 제네릭 명시
}
