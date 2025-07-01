package pack.modules.contentprovider.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pack.modules.contents.model.Contents;
import pack.modules.provides.model.Providers;

/* ContentProviders
 *
 * ○ 콘텐츠-플랫폼 매핑 테이블 Entity 클래스입니다.
 * ○ 다대다(N:M) 관계를 복합 키로 관리합니다.
 * ○ 연관 객체는 조회 전용으로 사용됩니다.
 */
@Entity
@Table(name = "content_providers")
@IdClass(ContentProvidersId.class)
@Getter
@Setter
public class ContentProviders {

    /* 콘텐츠 ID (복합 PK) */
    @Id
    @Column(name = "content_id")
    private int contentId;

    /* 플랫폼 ID (복합 PK) */
    @Id
    @Column(name = "provider_id")
    private int providerId;

    /* 콘텐츠 연관 객체 (조회 전용, insert/update 금지) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", insertable = false, updatable = false)
    private Contents contents;

    /* 플랫폼 연관 객체 (조회 전용, insert/update 금지) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Providers providers;
}
