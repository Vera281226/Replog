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

    /* TMDB ID 기준 콘텐츠 조회
     * - TMDB 연동 시 중복 방지에 사용
     */
    Optional<Contents> findByTmdbId(Integer tmdbId);

    /* 프론트 응답용 콘텐츠 전체 조회
     * - 플랫폼 정보는 서비스에서 개별 조회함
     */
    @Query("SELECT c FROM Contents c")
    List<Contents> findAllContentsForResponse();

    /* 단일 콘텐츠 ID 기준 조회
     * - 상세페이지 용도로 사용
     */
    @Query("SELECT c FROM Contents c WHERE c.contentId = :id")
    Contents findContentsWithId(@Param("id") Integer id);

    /* 플랫폼 이름 리스트 기준으로 콘텐츠 필터 조회
     * - platform 파라미터가 null 또는 비어 있으면 모든 콘텐츠 반환
     */
    @Query("""
        SELECT c FROM Contents c
        WHERE (:platforms IS NULL OR EXISTS (
            SELECT cp FROM ContentProviders cp
            JOIN Providers p ON cp.providerId = p.providerId
            WHERE cp.contentId = c.contentId AND p.name IN :platforms
        ))
    """)
    List<Contents> findByPlatformNames(@Param("platforms") List<String> platforms);
}
