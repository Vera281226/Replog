package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * TMDB Credits 응답 DTO
 * - /movie/{movie_id}/credits 또는 /tv/{tv_id}/credits
 * - 출연진(cast)과 제작진(crew)을 포함
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbCreditsDto {

    /**
     * 콘텐츠 ID
     */
    private int id;

    /**
     * 출연진 리스트
     */
    private List<Cast> cast;

    /**
     * 제작진 리스트
     */
    private List<Crew> crew;

    /**
     * 출연진 정보 클래스
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cast {

        /**
         * 인물 ID
         */
        private int id;

        /**
         * 인물 이름
         */
        private String name;

        /**
         * 캐릭터 이름 (출연 역할)
         */
        private String character;

        /**
         * 부서명 (예: Acting)
         */
        @JsonProperty("known_for_department")
        private String knownForDepartment;

        /**
         * 프로필 이미지 경로
         */
        @JsonProperty("profile_path")
        private String profilePath;
    }

    /**
     * 제작진 정보 클래스
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Crew {

        /**
         * 인물 ID
         */
        private int id;

        /**
         * 인물 이름
         */
        private String name;

        /**
         * 담당 직무 (예: Director)
         */
        private String job;

        /**
         * 소속 부서 (예: Directing) ← 이게 바로 getDepartment()용
         */
        private String department;

        /**
         * 부서명 (예: Directing)
         */
        @JsonProperty("known_for_department")
        private String knownForDepartment;

        /**
         * 프로필 이미지 경로
         */
        @JsonProperty("profile_path")
        private String profilePath;
    }
}
