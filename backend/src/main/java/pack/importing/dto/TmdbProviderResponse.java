package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * TmdbProviderResponse
 * -------------------------------------------------------------
 * ✅ TMDB 플랫폼 목록 응답을 매핑하는 DTO 클래스입니다.
 *
 * ✅ 사용 API: /watch/providers/movie
 * ❌ /watch/providers/tv (예능/TV용)는 사용하지 않습니다.
 *
 * ✅ 내부에 플랫폼 제공자 리스트를 포함하며,
 * ✅ 각 항목은 TmdbProviderDto로 매핑됩니다.
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbProviderResponse {
    @JsonProperty("results")
    private List<TmdbProviderDto> results = new ArrayList<>();  // 제네릭 명시
}
