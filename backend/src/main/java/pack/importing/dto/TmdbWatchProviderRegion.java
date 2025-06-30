package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * TMDB 국가별 플랫폼 제공 정보 DTO
 * <p>
 * /movie/{id}/watch/providers API에서 국가코드(KR 등)에 대응되는 지역 정보입니다.
 * <br>
 * 예: "KR": { "flatrate": [ {provider1}, {provider2} ] }
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbWatchProviderRegion {

    /**
     * 구독형(정액제) 플랫폼 목록
     * <p>
     * 예: Netflix, Disney+ 등
     */
    private List<TmdbProviderDto> flatrate;
}
