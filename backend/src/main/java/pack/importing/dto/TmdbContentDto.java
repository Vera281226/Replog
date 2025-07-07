package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * TmdbContentDto
 * -------------------------------------------------------------
 * ✅ TMDB 콘텐츠 1건을 매핑하는 DTO 클래스입니다.
 * ✅ TMDB API에서 받아온 JSON 응답을 자동으로 매핑합니다.
 * ✅ 별도의 @JsonProperty 없이 application 설정(camelCase) 기준 사용
 *
 * ✅ 영화(movie) vs 예능(tv) 구분 기준:
 * - 영화: title + releaseDate 사용
 * - 예능: name + firstAirDate 사용
 * -------------------------------------------------------------
 */
@Getter
@Setter
public class TmdbContentDto {

    // TMDB 콘텐츠 고유 ID
    private int id;

    // 콘텐츠 제목
    private String title;        // ✅ 영화용 필드 (영화: title)
    private String name;         // ✅ 예능/TV용 필드 (TV: name)

    // 줄거리
    private String overview;

    // 포스터 이미지 경로 (예: /abcd123.jpg)
    private String posterPath;

    // 배경 이미지 경로 (예: /backdrop123.jpg) → TV용에서 주로 사용
    private String backdropPath;

    // 개봉일
    private String releaseDate;   // ✅ 영화용 (영화: release_date)

    // 첫 방영일
    private String firstAirDate;  // ✅ 예능용 (TV: first_air_date)

    // TMDB 평점 (0.0 ~ 10.0)
    private double voteAverage;

    // 장르 ID 목록
    private List<Integer> genreIds;
}
