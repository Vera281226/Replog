package pack.modules.contentpeople.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 콘텐츠-인물 매핑의 복합 키 클래스입니다.
 * contentId + personId + role로 구성됩니다.
 */
@Getter
@Setter
@EqualsAndHashCode
public class ContentPeopleId implements Serializable {

    /** 콘텐츠 ID */
    private int contentId;

    /** 인물 ID */
    private int personId;

    /** 역할 (예: actor, director) */
    private String role;

    /** 기본 생성자 (JPA 필수) */
    public ContentPeopleId() {}

    /** 전체 필드 초기화 생성자 */
    public ContentPeopleId(int contentId, int personId, String role) {
        this.contentId = contentId;
        this.personId = personId;
        this.role = role;
    }
}
