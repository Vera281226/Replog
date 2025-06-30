package pack.modules.contentprovider.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 콘텐츠-플랫폼 매핑 요청 DTO
 * <p>
 * 클라이언트로부터 content_id와 provider_id를 받아 매핑을 저장하는 용도입니다.
 */
@Getter
@Setter
public class ContentProvidersRequest {

    /**
     * 콘텐츠 ID
     */
    private int contentId;

    /**
     * 플랫폼 ID
     */
    private int providerId;
}
