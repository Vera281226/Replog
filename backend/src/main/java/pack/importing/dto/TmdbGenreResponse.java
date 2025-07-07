package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// ==========================================
// TMDB 장르 응답 DTO
// - /genre/movie/list 또는 /genre/tv/list 응답 전체 구조 매핑
// - 예시 응답:
//   {
//     "genres": [
//       { "id": 28, "name": "액션" },
//       { "id": 35, "name": "코미디" }
//     ]
//   }
// ==========================================
@Getter
@Setter
public class TmdbGenreResponse {

    // 장르 목록
    // - JSON 키: "genres"
    // - 예: List<TmdbGenreDto>
    private List<TmdbGenreDto> genres;
}
