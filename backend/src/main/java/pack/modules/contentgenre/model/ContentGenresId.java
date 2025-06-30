package pack.modules.contentgenre.model;

import java.io.Serializable;              // 직렬화 인터페이스
import java.util.Objects;                 // equals(), hashCode() 도우미 클래스

/**
 * 복합키 클래스 for ContentGenres
 * content_id와 genre_id를 복합키로 사용
 */
public class ContentGenresId implements Serializable {

    /** 콘텐츠 ID */
    private int contentId;

    /** 장르 ID */
    private int genreId;

    /** 기본 생성자 */
    public ContentGenresId() {}

    /** 필드 생성자 */
    public ContentGenresId(int contentId, int genreId) {
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

    /**
     * equals 오버라이딩
     * 두 객체가 같은 contentId와 genreId를 가지는지 비교
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentGenresId)) return false;
        ContentGenresId that = (ContentGenresId) o;
        return contentId == that.contentId && genreId == that.genreId;
    }

    /**
     * hashCode 오버라이딩
     * 두 필드의 값을 기반으로 해시코드 생성
     */
    @Override
    public int hashCode() {
        return Objects.hash(contentId, genreId);
    }
}
