package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * TMDB의 Provider(제공 플랫폼) 개별 항목 DTO
 */
@Getter
@Setter
public class TmdbProviderDto {

    /**
     * 제공자 ID (TMDB의 provider_id와 매핑)
     */
    @JsonProperty("provider_id")
    private int providerId;

    /**
     * 제공자 이름 (TMDB의 provider_name과 매핑)
     */
    @JsonProperty("provider_name")
    private String providerName;

    /**
     * 로고 이미지 경로
     */
    private String logoPath;
}
