package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * TmdbCreditsDto
 * - TMDB Credits API 응답 구조
 * - 출연진(Cast), 제작진(Crew) 정보를 포함
 */
@Getter
@Setter
public class TmdbCreditsDto {

    // TMDB 콘텐츠 ID
    // - JSON 키: "id"
    // - 예: 123456
    private int id;

    // 출연진 리스트
    // - JSON 키: "cast"
    // - 각 항목은 Cast 클래스에 매핑됨
    private List<Cast> cast;

    // 제작진 리스트
    // - JSON 키: "crew"
    // - 각 항목은 Crew 클래스에 매핑됨
    private List<Crew> crew;

    // --------------------------
    // 출연진(Cast) 정보 클래스
    // --------------------------
    @Getter
    @Setter
    public static class Cast {

        // 출연진 인물 ID
        // - JSON 키: "id"
        private int id;

        // 이름
        // - JSON 키: "name"
        private String name;

        // 배역 이름
        // - JSON 키: "character"
        private String character;

        // 주요 활동 부서
        // - JSON 키: "known_for_department"
        private String knownForDepartment;

        // 프로필 이미지 경로
        // - JSON 키: "profile_path"
        private String profilePath;
    }

    // --------------------------
    // 제작진(Crew) 정보 클래스
    // --------------------------
    @Getter
    @Setter
    public static class Crew {

        // 제작진 인물 ID
        // - JSON 키: "id"
        private int id;

        // 이름
        // - JSON 키: "name"
        private String name;

        // 맡은 직무
        // - JSON 키: "job"
        private String job;

        // 부서
        // - JSON 키: "department"
        private String department;

        // 주요 활동 부서
        // - JSON 키: "known_for_department"
        private String knownForDepartment;

        // 프로필 이미지 경로
        // - JSON 키: "profile_path"
        private String profilePath;
    }
}
