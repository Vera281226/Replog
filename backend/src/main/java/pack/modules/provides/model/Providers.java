package pack.modules.provides.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/* Providers
 *
 * ○ OTT 공급자 정보를 나타내는 엔티티 클래스입니다.
 * ○ 공급자 ID, 이름, 로고 이미지 경로를 포함합니다.
 */
@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Providers {

    /* 공급자 고유 ID (PK, 수동 설정) */
    @Id
    @Column(name = "provider_id")
    private int providerId;

    /* 공급자 이름 (예: Netflix, Disney Plus) */
    @Column(name = "name", nullable = false)
    private String name;

    /* 공급자 로고 이미지 경로 */
    @Column(name = "logo_path")
    private String logoPath;
}
