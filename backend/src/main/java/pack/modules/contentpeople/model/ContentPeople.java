package pack.modules.contentpeople.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import pack.modules.contents.model.Contents;
import pack.modules.people.model.People;

/**
 * 콘텐츠와 인물 간의 다대다 매핑을 나타내는 엔티티 클래스입니다.
 * content_id, person_id, role을 복합키로 가지며, contents와 people 테이블을 연결합니다.
 */
@Entity
@Getter
@Setter
@Table(name = "content_people")
@IdClass(ContentPeopleId.class)
public class ContentPeople {

    /** 콘텐츠 ID (복합키 구성 요소) */
    @Id
    @Column(name = "content_id")
    private int contentId;

    /** 인물 ID (복합키 구성 요소) */
    @Id
    @Column(name = "person_id")
    private int personId;

    /** 역할 (복합키 구성 요소) */
    @Id
    @Column(name = "role")
    private String role;

    /** 캐릭터 이름 (배우일 경우에만 사용) */
    @Column(name = "character_name")
    private String characterName;

    /** 콘텐츠 연관 관계 (읽기 전용) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private Contents contents;

    /** 인물 연관 관계 (읽기 전용) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", insertable = false, updatable = false)
    private People people;

    /** 기본 생성자 */
    public ContentPeople() {}

    /** 전체 필드 생성자 */
    public ContentPeople(int contentId, int personId, String role, String characterName) {
        this.contentId = contentId;
        this.personId = personId;
        this.role = role;
        this.characterName = characterName;
    }
}
