package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * TmdbContentDto
 * -------------------------------------------------------------
 * ✅ TMDB 콘텐츠 1건을 매핑하는 DTO 클래스입니다.
 * ✅ 영화(movie) 콘텐츠 전용이며, 예능(tv)은 포함하지 않습니다.
 * ✅ Jackson 자동 매핑 (snake_case → camelCase)을 사용하므로
 *    별도의 @JsonProperty는 사용하지 않습니다.
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbContentDto {
    private int id;
    private String title;
    private String overview;
    
    @JsonProperty("poster_path")
    private String posterPath;
    
    @JsonProperty("backdrop_path")
    private String backdropPath;
    
    @JsonProperty("release_date")
    private String releaseDate;
    
    @JsonProperty("vote_average")
    private double voteAverage;
    
    @JsonProperty("genre_ids")
    private List<Integer> genreIds = new ArrayList<>();  // 기본값으로 빈 리스트 초기화
    
    private Integer runtime;
    
    @JsonProperty("age_rating")
    private String ageRating;
    
    // Null-Safe Getter
    public List<Integer> getGenreIds() {
        return genreIds != null ? genreIds : new ArrayList<>();
    }
    
 // ✅ Runtime Null-Safe Getter
    public Integer getRuntime() {
        return runtime != null ? runtime : 0;
    }
}

