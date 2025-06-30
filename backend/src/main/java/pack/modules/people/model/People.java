package pack.modules.people.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 인물 정보를 나타내는 엔티티 클래스입니다.
 * - DB 컬럼은 snake_case
 * - Java 필드는 camelCase
 */
@Entity
@Getter
@Setter
@Table(name = "people")
public class People {

    /** 내부 PK (AUTO_INCREMENT) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id") // ✅ 수정됨
    private int personId;

    /** TMDB 고유 ID */
    @Column(name = "tmdb_id", unique = true)
    private int tmdbId;

    /** 인물 이름 */
    @Column(name = "name")
    private String name;

    /** 프로필 이미지 경로 */
    @Column(name = "profile_path")
    private String profilePath;

    /** 주요 활동 분야 (예: Acting, Directing) */
    @Column(name = "known_for_department")
    private String knownForDepartment;
}
