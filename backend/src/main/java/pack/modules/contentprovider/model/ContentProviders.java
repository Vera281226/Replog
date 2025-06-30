package pack.modules.contentprovider.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pack.modules.contents.model.Contents;
import pack.modules.provides.model.Providers;

/**
 * 콘텐츠-플랫폼 매핑 테이블 Entity
 * <p>
 * - 다대다 관계 매핑 (복합 키 사용)
 * - 조회용 연관 객체 포함 (insertable = false, updatable = false)
 */
@Entity
@Table(name = "content_providers")
@IdClass(ContentProvidersId.class)
@Getter
@Setter
public class ContentProviders {

    /**
     * 콘텐츠 ID (PK)
     */
    @Id
    @Column(name = "content_id")
    private int contentId;

    /**
     * 플랫폼 ID (PK)
     */
    @Id
    @Column(name = "provider_id")
    private int providerId;

    /**
     * 콘텐츠 연관 객체 (조회 전용)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private Contents contents;

    /**
     * 플랫폼 연관 객체 (조회 전용)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Providers providers;
}
