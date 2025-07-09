package pack.modules.contentprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pack.modules.contentprovider.model.ContentProviders;
import pack.modules.contentprovider.model.ContentProvidersId;

import java.util.List;

/**
 * 콘텐츠-플랫폼 매핑 Repository
 * -------------------------------------------------------------
 * ✅ 복합 키: content_id + provider_id 기반
 * ✅ 콘텐츠별 플랫폼 매핑 정보 조회용
 * ✅ 플랫폼 ID/이름 조회 메서드 포함
 * -------------------------------------------------------------
 */
public interface ContentProvidersRepository extends JpaRepository<ContentProviders, ContentProvidersId> {

	/**
	 * ✅ 특정 콘텐츠-플랫폼 매핑 존재 여부
	 * @param contentId  콘텐츠 ID
	 * @param providerId 플랫폼 ID
	 * @return 존재 여부 (true/false)
	 */
	boolean existsByContentIdAndProviderId(int contentId, int providerId);

	/**
	 * ✅ 콘텐츠 ID 기준 플랫폼 이름 목록 조회
	 * @param contentId 콘텐츠 ID
	 * @return 플랫폼 이름 리스트 (예: ["넷플릭스", "디즈니+"])
	 */
	@Query("""
        SELECT p.name FROM ContentProviders cp
        JOIN Providers p ON cp.providerId = p.providerId
        WHERE cp.contentId = :contentId
    """)
	List<String> findProviderNamesByContentId(@Param("contentId") Integer contentId);

	/**
	 * ✅ 콘텐츠 ID 기준 플랫폼 ID 목록 조회
	 * @param contentId 콘텐츠 ID
	 * @return 플랫폼 ID 리스트 (예: [8, 337])
	 */
	@Query("""
        SELECT p.providerId FROM ContentProviders cp
        JOIN Providers p ON cp.providerId = p.providerId
        WHERE cp.contentId = :contentId
    """)
	List<Integer> findProviderIdsByContentId(@Param("contentId") Integer contentId);
}
