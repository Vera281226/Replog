package pack.modules.people.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * People 응답 DTO 클래스
 * 클라이언트에 전달되는 인물 정보를 담습니다.
 */
@Getter
@Setter
public class PeopleResponse {

    /** 내부 고유 ID */
    private int personId;

    /** TMDB 인물 ID */
    private int tmdbId;

    /** 인물 이름 */
    private String name;

    /** 프로필 이미지 경로 */
    private String profilePath;

    /** 활동 부서 */
    private String knownForDepartment;
}
