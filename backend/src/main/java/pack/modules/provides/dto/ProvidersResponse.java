package pack.modules.provides.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * OTT 공급자 조회 응답용 DTO
 * - 서버 → 클라이언트
 */
@Getter
@Setter
public class ProvidersResponse {
    private int providerId;    // 공급자 ID
    private String name;       // 공급자 이름
    private String logoPath;   // 로고 경로
}
