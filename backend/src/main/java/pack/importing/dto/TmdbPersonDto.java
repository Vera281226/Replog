package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * TmdbPersonDto
 * -------------------------------------------------------------
 * ○ TMDB 출연진 / 제작진 정보를 매핑하는 DTO 클래스입니다.
 * ○ API: /movie/{id}/credits 또는 /tv/{id}/credits 응답 항목에 사용됩니다.
 * ○ 불필요한 어노테이션 없이, 필드명만 camelCase로 구성해 자동 매핑합니다.
 * -------------------------------------------------------------
 */
@Getter
@Setter
public class TmdbPersonDto {

    // TMDB 인물 고유 ID
    // - JSON 키: "id"
    // - 예: 123456
    private int id;

    // 인물 이름
    // - JSON 키: "name"
    // - 예: "로버트 다우니 주니어"
    private String name;

    // 등장 캐릭터 이름 (출연진일 경우)
    // - JSON 키: "character"
    // - 예: "토니 스타크"
    private String character;

    // 소속 부서 (제작진일 경우)
    // - JSON 키: "department"
    // - 예: "Directing", "Writing"
    private String department;

    // 활동 분야 (출연/제작 구분)
    // - JSON 키: "known_for_department"
    // - 예: "Acting", "Production"
    private String knownForDepartment;

    // 프로필 이미지 경로 (TMDB 상대 경로)
    // - JSON 키: "profile_path"
    // - 예: "/abc123.jpg"
    private String profilePath;
}
