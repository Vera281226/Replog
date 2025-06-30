package pack.modules.contentprovider.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 콘텐츠-플랫폼 매핑 응답 DTO
 * <p>
 * 매핑된 content_id, provider_id를 반환하는 용도입니다.
 */
@Getter
@Setter
public class ContentProvidersResponse {

    /**
     * 콘텐츠 ID
     */
    private int contentId;

    /**
     * 플랫폼 ID
     */
    private int providerId;
}
