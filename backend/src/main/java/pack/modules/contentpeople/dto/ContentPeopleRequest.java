package pack.modules.contentpeople.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 콘텐츠-인물 매핑 등록 요청을 담는 DTO 클래스입니다.
 */
@Getter
@Setter
public class ContentPeopleRequest {

    /** 콘텐츠 ID */
    private int contentId;

    /** 인물 ID */
    private int personId;

    /** 역할 (예: actor, director) */
    private String role;

    /** 캐릭터 이름 (배우일 경우에만 입력) */
    private String characterName;
}
