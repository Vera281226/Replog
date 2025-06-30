package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * TMDB 출연진 및 제작진 단일 인물 DTO
 * <p>
 * 이 클래스는 /movie/{id}/credits 또는 /tv/{id}/credits API의 cast, crew 배열 요소를 매핑합니다.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbPersonDto {

    /**
     * 인물 고유 ID (person_id)
     */
    private int id;

    /**
     * 인물 이름
     */
    private String name;

    /**
     * 출연진인 경우 등장 캐릭터 이름
     */
    private String character;

    /**
     * 제작진인 경우 부서 (예: Directing, Writing)
     */
    private String department;

    /**
     * 출연 또는 제작 분야 (예: Acting, Directing)
     */
    @JsonProperty("known_for_department")
    private String knownForDepartment;

    /**
     * 프로필 이미지 경로 (상대 경로, TMDB 기준)
     */
    @JsonProperty("profile_path")
    private String profilePath;
}
