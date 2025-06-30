package pack.modules.people.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * PeopleRequest
 * <p>
 * 인물 등록 및 수정 요청을 처리하기 위한 DTO 클래스입니다.
 * 클라이언트에서 전달된 인물 정보를 담아 서버로 전송할 때 사용됩니다.
 *
 * <p><b>사용 목적:</b>
 * - 신규 인물 추가
 * - 기존 인물 수정
 */
@Getter
@Setter
public class PeopleRequest {

    /** TMDB 고유 ID (중복 방지용, DB의 tmdb_id에 해당) */
    private int tmdbId;

    /** 인물 이름 */
    private String name;

    /** 프로필 이미지 경로 (예: /path/to/profile.jpg) */
    private String profilePath;

    /** 인물 활동 부서 (예: Acting, Directing 등) */
    private String knownForDepartment;
}
