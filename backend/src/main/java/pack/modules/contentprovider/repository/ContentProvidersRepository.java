package pack.modules.contentprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pack.modules.contentprovider.model.ContentProviders;
import pack.modules.contentprovider.model.ContentProvidersId;

import java.util.List;

/**
 * 콘텐츠-플랫폼 매핑 Repository
 * <p>
 * 복합 키: content_id + provider_id
 */
public interface ContentProvidersRepository extends JpaRepository<ContentProviders, ContentProvidersId> {

    /**
     * 특정 매핑이 존재하는지 확인
     *
     * @param contentId  콘텐츠 ID
     * @param providerId 플랫폼 ID
     * @return 존재 여부
     */
    boolean existsByContentIdAndProviderId(int contentId, int providerId);

    /**
     * 콘텐츠 ID 기준으로 연결된 플랫폼 이름 목록을 조회합니다.
     * - 플랫폼 이름만 리스트로 추출 (JPQL 기반)
     */
    @Query("""
    	    SELECT p.name FROM ContentProviders cp
    	    JOIN Providers p ON cp.providerId = p.providerId
    	    WHERE cp.contentId = :contentId
    	    """)
    	    List<String> findProviderNamesByContentId(@Param("contentId") Integer contentId);
    
    
}
