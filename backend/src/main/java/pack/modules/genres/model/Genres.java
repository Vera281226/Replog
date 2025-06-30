package pack.modules.genres.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * genres 테이블에 매핑되는 JPA 엔티티 클래스
 */
@Entity
@Table(name = "genres")
@Getter
@Setter
public class Genres {

    /** 장르 고유 ID (기본키, 자동 증가 아님) */
    @Id
    @Column(name = "genre_id", nullable = false)
    private Integer genreId;

    /** 장르 이름 (예: 액션, 드라마 등) */
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    /**
     * 장르 레코드가 생성된 날짜 및 시간
     * - DB에서 자동으로 CURRENT_TIMESTAMP 값을 넣음
     * - JPA에서는 값을 직접 넣거나 수정하지 않음 (읽기 전용)
     * - insertable = false: INSERT 시 JPA가 이 필드를 무시함
     * - updatable = false: UPDATE 시에도 이 값을 수정하지 않음
     */
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
