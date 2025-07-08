package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * TmdbGenreResponse
 * -------------------------------------------------------------
 * ✅ TMDB 장르 전체 응답을 매핑하는 DTO 클래스입니다.
 *
 * ✅ /genre/movie/list API 응답만 처리합니다.
 * ❌ /genre/tv/list (예능/TV용) 응답은 사용하지 않습니다.
 *
 * 예시 응답:
 * {
 *   "genres": [
 *     { "id": 28, "name": "액션" },
 *     { "id": 35, "name": "코미디" }
 *   ]
 * }
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbGenreResponse {
    @JsonProperty("genres")
    private List<TmdbGenreDto> genres = new ArrayList<>();  // 제네릭 명시
}
