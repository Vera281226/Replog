package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TmdbContentDto
 * ------------------------------------------------------------------
 * 1) TMDB 콘텐츠(영화·TV) 한 건을 매핑하는 DTO 클래스입니다.
 * 2) 영화(movie)는 물론 TV 시리즈(tv) 응답도 모두 수용하도록 설계했습니다.
 * 3) Jackson의 snake_case → camelCase 자동 매핑을 사용하며,
 *    꼭 필요한 필드에만 @JsonProperty를 지정했습니다.
 * 4) discover API(genre_ids)와 detail API(genres 객체 배열) 양쪽을
 *    모두 처리할 수 있도록 통합 메서드를 제공합니다.
 * ------------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbContentDto {

    /* ----------------------------------------------------------------
     * 공통 기본 필드
     * ---------------------------------------------------------------- */
    private int id;                 // TMDB 고유 ID

    /* ----------------------------------------------------------------
     * 제목(영화·TV 구분)
     * ----------------------------------------------------------------
     * - 영화:  title
     * - TV  :  name
     * ---------------------------------------------------------------- */
    private String title;           // 영화용
    private String name;            // TV용

    private String overview;        // 줄거리

    @JsonProperty("poster_path")
    private String posterPath;      // 포스터 경로

    @JsonProperty("backdrop_path")
    private String backdropPath;    // 배경 이미지 경로

    /* ----------------------------------------------------------------
     * 개봉·방영 날짜(영화·TV 구분)
     * ----------------------------------------------------------------
     * - 영화:  release_date
     * - TV  :  first_air_date
     * ---------------------------------------------------------------- */
    @JsonProperty("release_date")
    private String releaseDate;     // 영화용

    @JsonProperty("first_air_date")
    private String firstAirDate;    // TV용

    /* ----------------------------------------------------------------
     * 평점
     * ---------------------------------------------------------------- */
    @JsonProperty("vote_average")
    private double voteAverage;     // TMDB 평점

    /* ----------------------------------------------------------------
     * 장르 정보
     * ----------------------------------------------------------------
     * 1) discover/movie, discover/tv 에서는 'genre_ids' 정수 배열 형태
     * 2) movie/{id}, tv/{id}  상세 API에서는 'genres' 객체 배열 형태
     * ---------------------------------------------------------------- */
    @JsonProperty("genre_ids")
    private List<Integer> genreIds = new ArrayList<>();     // 정수형 ID 리스트

    private List<GenreObject> genres = new ArrayList<>();   // 객체 배열용

    /* ----------------------------------------------------------------
     * 런타임(영화·TV 구분)
     * ----------------------------------------------------------------
     * - 영화:  runtime (단일 정수, 분 단위)
     * - TV  :  episode_run_time (정수 배열, 첫 번째 값 사용)
     * ---------------------------------------------------------------- */
    private Integer runtime;                                // 영화 runtime

    @JsonProperty("episode_run_time")
    private List<Integer> episodeRunTime = new ArrayList<>();  // TV runtime 배열

    /* ----------------------------------------------------------------
     * 연령 등급(커스텀 필드, 서비스 로직에서 주입)
     * ---------------------------------------------------------------- */
    @JsonProperty("age_rating")
    private String ageRating;

    /* =================================================================
     * 내부 클래스: 상세 API의 genres 객체 배열 매핑용
     * ================================================================= */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GenreObject {
        private int id;
        private String name;
    }

    /* =================================================================
     * 유틸리티 메서드
     * ================================================================= */

    /** discover API용 Null-Safe 장르 ID 리스트 반환 */
    public List<Integer> getGenreIds() {
        return genreIds != null ? genreIds : new ArrayList<>();
    }

    /** detail API의 genres 객체 배열에서 ID만 추출 */
    public List<Integer> extractGenreIdsFromGenres() {
        if (genres != null && !genres.isEmpty()) {
            return genres.stream()
                         .map(GenreObject::getId)
                         .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /** discover·detail 양쪽을 통합한 최종 장르 ID 리스트 반환 */
    public List<Integer> getAllGenreIds() {
        List<Integer> result = getGenreIds();
        if (result.isEmpty()) {
            result = extractGenreIdsFromGenres();
        }
        return result;
    }

    /** 영화·TV를 구분하지 않고 '제목'을 단일 값으로 반환 */
    public String getUnifiedTitle() {
        return title != null && !title.isBlank() ? title : name;
    }

    /** 영화·TV를 구분하지 않고 '개봉·첫방영일'을 단일 값으로 반환 */
    public String getUnifiedReleaseDate() {
        return releaseDate != null && !releaseDate.isBlank() ? releaseDate : firstAirDate;
    }

    /** 영화·TV를 구분하지 않고 '런타임'을 단일 Integer 값으로 반환 */
    public Integer getUnifiedRuntime() {
        if (runtime != null && runtime > 0) {
            return runtime;
        }
        if (episodeRunTime != null && !episodeRunTime.isEmpty() && episodeRunTime.get(0) > 0) {
            return episodeRunTime.get(0);
        }
        return null; // 값이 없으면 null 반환
    }
}
