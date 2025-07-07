package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

// ==========================================
// TMDB 장르 DTO
// - TMDB API의 단일 장르 응답을 매핑
// - 사용 위치: /genre/movie/list, /genre/tv/list
// - 예시 응답: { "id": 28, "name": "액션" }
// ==========================================
@Getter
@Setter
public class TmdbGenreDto {

    // 장르 고유 ID
    // - JSON 키: "id"
    // - 예: 28
    private int id;

    // 장르 이름
    // - JSON 키: "name"
    // - 예: "액션", "코미디", "드라마"
    private String name;
}
