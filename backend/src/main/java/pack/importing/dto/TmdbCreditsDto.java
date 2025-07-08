package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * TmdbCreditsDto
 * -------------------------------------------------------------
 * ✅ TMDB Credits API 응답 구조
 * ✅ 출연진(Cast), 제작진(Crew) 정보를 포함합니다.
 * ✅ 영화(movie) 콘텐츠 전용 구조입니다.
 * ❌ 예능(tv) 관련 필드는 포함되지 않습니다.
 * ✅ snake_case → camelCase 자동 매핑으로 구성됨
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbCreditsDto {
    private int id;
    private List<Cast> cast = new ArrayList<>();  // 제네릭 명시
    private List<Crew> crew = new ArrayList<>();  // 제네릭 명시
    
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cast {
        private int id;
        private String name;
        private String character;
        
        @JsonProperty("known_for_department")
        private String knownForDepartment;
        
        @JsonProperty("profile_path")
        private String profilePath;
        
        @JsonProperty("cast_id")
        private Integer castId;
        
        private Integer order;
    }
    
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Crew {
        private int id;
        private String name;
        private String job;
        private String department;
        
        @JsonProperty("known_for_department")
        private String knownForDepartment;
        
        @JsonProperty("profile_path")
        private String profilePath;
    }
}
