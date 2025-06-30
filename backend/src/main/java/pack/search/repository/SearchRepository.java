package pack.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pack.modules.contents.model.Contents;
import pack.search.dto.SearchResponse;

import java.util.List;

/**
 * SearchRepository 인터페이스
 * - contents 테이블에서 제목 기반 검색 기능을 제공
 */
public interface SearchRepository extends JpaRepository<Contents, Integer> {

    /**
     * 제목에 키워드가 포함된 콘텐츠 검색
     * - title LIKE %:keyword% 조건으로 검색 (대소문자 무시)
     * - 결과는 SearchResponse DTO로 매핑됨
     * - release_date는 String으로 DTO에 매핑됨 (DATE → String 자동 변환됨)
     */
    @Query("""
        SELECT new pack.search.dto.SearchResponse(
            c.contentId,
            c.title,
            c.overview,
            c.posterPath,
            c.releaseDate,
            c.rating,
            c.mediaType
        )
        FROM Contents c
        WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY c.releaseDate DESC
    """)
    List<SearchResponse> searchByTitleContaining(@Param("keyword") String keyword);
}