package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TmdbContentResponse
 * -------------------------------------------------------------
 * ✅ TMDB 콘텐츠 목록 응답을 매핑하는 DTO 클래스입니다.
 * ✅ TMDB API의 "results" 배열만 사용하며, 다른 필드는 무시합니다.
 *
 * ✅ 영화(movie) 콘텐츠 목록에만 사용됩니다.
 * ❌ 예능(tv) 콘텐츠는 포함되지 않습니다.
 * ✅ 내부 요소는 TmdbContentDto로 자동 매핑됩니다.
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbContentResponse {
    @JsonProperty("results")
    private List<TmdbContentDto> results = new ArrayList<>();  // 제네릭 명시 및 초기화
    
    private int page;
    
    @JsonProperty("total_pages")
    private int totalPages;
    
    @JsonProperty("total_results")
    private int totalResults;
}
