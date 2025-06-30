package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *  TMDB 전체 장르 응답을 감싸는 DTO
 * 예: { "genres": [ { "id": 28, "name": "액션" }, ... ] }
 */
@Getter
@Setter
public class TmdbGenreResponse {
    private List<TmdbGenreDto> genres;
}
