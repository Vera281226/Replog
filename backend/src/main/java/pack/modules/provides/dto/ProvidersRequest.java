package pack.modules.provides.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * OTT 공급자 등록/수정 요청용 DTO
 * - 클라이언트 → 서버
 */
@Getter
@Setter
public class ProvidersRequest {
    private int providerId;    // 공급자 ID (직접 입력)
    private String name;       // 공급자 이름
    private String logoPath;   // 로고 경로 (nullable)
}
