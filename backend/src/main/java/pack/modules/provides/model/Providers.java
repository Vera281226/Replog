package pack.modules.provides.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * OTT 공급자 엔티티
 * - provider_id: 공급자 ID (PK, 직접 설정)
 * - name: 공급자 이름 (NOT NULL)
 * - logoPath: 로고 이미지 경로
 */
@Entity
@Table(name = "providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Providers {

    @Id
    @Column(name = "provider_id")
    private int providerId;  // 플랫폼 ID

    @Column(name = "name", nullable = false)
    private String name;     // 플랫폼 이름

    @Column(name = "logo_path")
    private String logoPath; // 로고 이미지 경로
}
