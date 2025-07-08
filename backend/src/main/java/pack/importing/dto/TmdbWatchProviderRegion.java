package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * TmdbWatchProviderRegion
 * -------------------------------------------------------------
 * ✅ TMDB 국가별 OTT 제공자 목록을 매핑하는 DTO 클래스입니다.
 *
 * ✅ 사용 API: /movie/{id}/watch/providers
 * ❌ /tv/{id}/watch/providers (예능/TV용)는 사용하지 않습니다.
 *
 * ✅ 특정 국가(KR 등)의 제공자 정보를 지역 단위로 처리합니다.
 *
 * 예시 응답:
 * {
 *   "KR": {
 *     "flatrate": [
 *       {
 *         "provider_id": 337,
 *         "provider_name": "Disney Plus",
 *         "logo_path": "/abc.png"
 *       }
 *     ]
 *   }
 * }
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbWatchProviderRegion {
    @JsonProperty("flatrate")
    private List<TmdbProviderDto> flatrate = new ArrayList<>();  // 제네릭 명시
    
    @JsonProperty("rent")
    private List<TmdbProviderDto> rent = new ArrayList<>();
    
    @JsonProperty("buy")
    private List<TmdbProviderDto> buy = new ArrayList<>();
    
    private String link;
}
