package pack.modules.contentprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.modules.contentprovider.model.ContentProviders;
import pack.modules.contentprovider.model.ContentProvidersId;

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
}
