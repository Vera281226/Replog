package pack.modules.contents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pack.modules.contents.model.Contents;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentsRepository extends JpaRepository<Contents, Integer> {

    // -------------------------------------------------------
    // ✅ TMDB ID 기준 콘텐츠 조회
    // - TMDB 연동 시 중복 저장 방지용
    // -------------------------------------------------------
    Optional<Contents> findByTmdbId(Integer tmdbId);

    // -------------------------------------------------------
    // ✅ 전체 콘텐츠 단순 조회 (프론트 전체 보기용)
    // - 플랫폼 정보는 서비스에서 개별 조회
    // -------------------------------------------------------
    @Query("SELECT c FROM Contents c")
    List<Contents> findAllContentsForResponse();

    // -------------------------------------------------------
    // ✅ 콘텐츠 상세 조회 (ID 기반)
    // - 상세페이지 등 단일 콘텐츠 출력용
    // -------------------------------------------------------
    @Query("SELECT c FROM Contents c WHERE c.contentId = :id")
    Contents findContentsWithId(@Param("id") Integer id);

    // -------------------------------------------------------
    // ✅ 디즈니+ 플랫폼 콘텐츠 전용 JPQL 쿼리
    // - 프론트 index 페이지 디즈니 섹션에 사용
    // -------------------------------------------------------
    @Query("""
        SELECT c FROM Contents c
        WHERE EXISTS (
            SELECT cp FROM ContentProviders cp
            JOIN Providers p ON cp.providerId = p.providerId
            WHERE cp.contentId = c.contentId AND p.name = 'Disney Plus'
        )
        ORDER BY c.rating DESC
    """)
    List<Contents> findDisneyContents();

    // -------------------------------------------------------
    // ✅ 넷플릭스 플랫폼 콘텐츠 전용 JPQL 쿼리
    // - 프론트 index 페이지 넷플릭스 섹션에 사용
    // -------------------------------------------------------
    @Query("""
        SELECT c FROM Contents c
        WHERE EXISTS (
            SELECT cp FROM ContentProviders cp
            JOIN Providers p ON cp.providerId = p.providerId
            WHERE cp.contentId = c.contentId AND p.name = 'Netflix'
        )
        ORDER BY c.rating DESC
    """)
    List<Contents> findNetflixContents();

    // -------------------------------------------------------
    // ✅ 개봉 예정 콘텐츠 조회 (release_date 기준)
    // - 오늘 이후의 영화/TV만 추출하여 release_date 오름차순 정렬
    // -------------------------------------------------------
    @Query("""
        SELECT c FROM Contents c
        WHERE c.releaseDate > CURRENT_DATE
        ORDER BY c.releaseDate ASC
    """)
    List<Contents> findUpcomingContents();

    // -------------------------------------------------------
    // ✅ 플랫폼 이름 리스트 기반 콘텐츠 필터링
    // - :platforms 파라미터가 null 또는 비어있으면 전체 반환
    // -------------------------------------------------------
    @Query("""
        SELECT c FROM Contents c
        WHERE (:platforms IS NULL OR EXISTS (
            SELECT cp FROM ContentProviders cp
            JOIN Providers p ON cp.providerId = p.providerId
            WHERE cp.contentId = c.contentId AND p.name IN :platforms
        ))
    """)
    List<Contents> findByPlatformNames(@Param("platforms") List<String> platforms);

    // -------------------------------------------------------
    // ✅ 장르 ID 기반 콘텐츠 필터링
    // - 단일 또는 다중 장르를 기반으로 콘텐츠 조회
    // -------------------------------------------------------
    @Query("""
        SELECT DISTINCT c FROM Contents c
        JOIN ContentGenres cg ON c.contentId = cg.contentId
        WHERE (:genreIds IS NULL OR cg.genreId IN :genreIds)
    """)
    List<Contents> findByGenreIds(@Param("genreIds") List<Integer> genreIds);

    // -------------------------------------------------------
    // ✅ 장르 + 플랫폼 동시 필터링 (복합 조건)
    // - genreIds, platforms 모두 고려하여 콘텐츠 필터링
    // -------------------------------------------------------
    @Query("""
        SELECT DISTINCT c FROM Contents c
        JOIN ContentGenres cg ON c.contentId = cg.contentId
        JOIN ContentProviders cp ON c.contentId = cp.contentId
        JOIN Providers p ON cp.providerId = p.providerId
        WHERE (:genreIds IS NULL OR cg.genreId IN :genreIds)
          AND (:platforms IS NULL OR p.name IN :platforms)
    """)
    List<Contents> findByGenreIdsAndPlatforms(
            @Param("genreIds") List<Integer> genreIds,
            @Param("platforms") List<String> platforms
    );

    // -------------------------------------------------------
    // ✅ 예능(tv) 콘텐츠 조회 (media_type = 'tv')
    // - 프론트 예능 전용 섹션에 사용
    // -------------------------------------------------------
    @Query("""
        SELECT c FROM Contents c
        WHERE c.mediaType = 'tv'
        ORDER BY c.rating DESC
    """)
    List<Contents> findVarietyShows();
}
