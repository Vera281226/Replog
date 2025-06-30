package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *  TMDB 장르 응답 중 한 개의 장르 항목을 담는 DTO
 * 예: { "id": 28, "name": "액션" }
 */
@Getter
@Setter
public class TmdbGenreDto {
    private int id;        // TMDB 장르 ID
    private String name;   // 장르 이름
}
