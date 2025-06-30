package pack.modules.contentpeople.dto;

import lombok.Getter;
import lombok.Setter;
import pack.modules.contentpeople.model.ContentPeople;

/**
 * 콘텐츠-인물 매핑 응답 정보를 담는 DTO 클래스입니다.
 */
@Getter
@Setter
public class ContentPeopleResponse {

    /** 콘텐츠 ID */
    private int contentId;

    /** 인물 ID */
    private int personId;

    /** 역할 (예: actor, director) */
    private String role;

    /** 캐릭터 이름 */
    private String characterName;

    /** 인물 이름 */
    private String personName;

    /** 인물 프로필 이미지 경로 */
    private String profilePath;

    /** 인물 활동 분야 */
    private String knownForDepartment;

    /**
     * Entity → DTO 변환 생성자
     */
    public ContentPeopleResponse(ContentPeople entity) {
        this.contentId = entity.getContentId();
        this.personId = entity.getPersonId();
        this.role = entity.getRole();
        this.characterName = entity.getCharacterName();

        // 연관 엔티티 People 정보 (조회용 필드에서 꺼내기)
        if (entity.getPeople() != null) {
            this.personName = entity.getPeople().getName();
            this.profilePath = entity.getPeople().getProfilePath();
            this.knownForDepartment = entity.getPeople().getKnownForDepartment();
        }
    }

    // 기본 생성자
    public ContentPeopleResponse() {}
}
