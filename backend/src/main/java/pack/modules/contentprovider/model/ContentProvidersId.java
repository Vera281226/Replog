package pack.modules.contentprovider.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * ContentProviders의 복합 키 클래스
 * - content_id + provider_id 복합키를 사용
 */
@Getter
@Setter
@EqualsAndHashCode
public class ContentProvidersId implements Serializable {

    private int contentId;
    private int providerId;

    // 필수: 기본 생성자 (JPA용)
    public ContentProvidersId() {}

    // ✅ 오류 해결: 생성자 추가
    public ContentProvidersId(int contentId, int providerId) {
        this.contentId = contentId;
        this.providerId = providerId;
    }
}
