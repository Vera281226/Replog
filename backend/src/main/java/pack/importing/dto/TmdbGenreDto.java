package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * TmdbGenreDto
 * -------------------------------------------------------------
 * ✅ TMDB 장르 1건 응답을 매핑하는 DTO 클래스입니다.
 * ✅ TMDB API의 "genres" 배열 내 항목 중 1개
 *
 * ✅ 영화(movie) 전용 장르 목록에만 사용됩니다.
 * ❌ 예능(tv) 장르 응답(/genre/tv/list)은 사용하지 않습니다.
 * ✅ @JsonProperty 없이 snake_case → camelCase 자동 매핑 사용
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbGenreDto {

    // 장르 ID (예: 28)
    private int id;

    // 장르 이름 (예: "액션", "코미디")
    private String name;
}
