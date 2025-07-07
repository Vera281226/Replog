package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * TmdbProviderDto
 * -------------------------------------------------------------
 * ✅ TMDB에서 제공하는 플랫폼 제공자 정보를 매핑하는 DTO 클래스입니다.
 * ✅ 사용 API: /movie/{id}/watch/providers 응답 중 flatrate 항목
 *
 * ✅ snake_case → camelCase 자동 매핑 방식 사용
 * ✅ 예능(tv), 광고(ad) 관련 항목은 포함하지 않음
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbProviderDto {
    @JsonProperty("provider_id")
    private int providerId;
    
    @JsonProperty("provider_name")
    private String providerName;
    
    @JsonProperty("logo_path")
    private String logoPath;
}