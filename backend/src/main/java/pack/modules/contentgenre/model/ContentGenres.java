package pack.modules.contentgenre.model;

import jakarta.persistence.Column;         // DB 컬럼 매핑
import jakarta.persistence.Entity;         // 엔티티 지정
import jakarta.persistence.FetchType;      // 지연 로딩 설정
import jakarta.persistence.Id;             // 기본키 지정
import jakarta.persistence.IdClass;        // 복합키 클래스 지정
import jakarta.persistence.JoinColumn;     // FK 조인 설정
import jakarta.persistence.ManyToOne;      // 다대일 관계 매핑
import jakarta.persistence.Table;          // 테이블명 매핑
import pack.modules.contents.model.Contents;
import pack.modules.genres.model.Genres;

/**
 * 콘텐츠-장르 매핑 엔티티 클래스
 * content_id와 genre_id를 복합키로 가지며, contents와 genres 테이블을 연결함
 */
@Entity
@IdClass(ContentGenresId.class)
@Table(name = "content_genres")
public class ContentGenres {

    /** 콘텐츠 ID (복합키 구성 요소) */
    @Id
    @Column(name = "content_id")
    private int contentId;

    /** 장르 ID (복합키 구성 요소) */
    @Id
    @Column(name = "genre_id")
    private int genreId;

    /** 콘텐츠 연관 관계 (읽기 전용) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private Contents contents;

    /** 장르 연관 관계 (읽기 전용) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", insertable = false, updatable = false)
    private Genres genres;

    /** 기본 생성자 */
    public ContentGenres() {}

    /** 필드 생성자 */
    public ContentGenres(int contentId, int genreId) {
        this.contentId = contentId;
        this.genreId = genreId;
    }

    /** 콘텐츠 ID 반환 */
    public int getContentId() {
        return contentId;
    }

    /** 콘텐츠 ID 설정 */
    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    /** 장르 ID 반환 */
    public int getGenreId() {
        return genreId;
    }

    /** 장르 ID 설정 */
    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    /** 콘텐츠 엔티티 반환 */
    public Contents getContents() {
        return contents;
    }

    /** 콘텐츠 엔티티 설정 */
    public void setContents(Contents contents) {
        this.contents = contents;
    }

    /** 장르 엔티티 반환 */
    public Genres getGenres() {
        return genres;
    }

    /** 장르 엔티티 설정 */
    public void setGenres(Genres genres) {
        this.genres = genres;
    }
}
