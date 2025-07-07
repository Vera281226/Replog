package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * TmdbProviderDto
 * -------------------------------------------------------------
 * ○ TMDB에서 제공하는 플랫폼 제공자 정보를 매핑하는 DTO 클래스입니다.
 * ○ 사용 API: /watch/providers 하위 응답 항목 중 단일 플랫폼 정보를 처리합니다.
 * ○ 어노테이션 없이 camelCase 필드명 기반 자동 매핑 구조로 구성합니다.
 * -------------------------------------------------------------
 */
@Getter
@Setter
public class TmdbProviderDto {

    // 플랫폼 제공자 ID
    // - JSON 키: "provider_id"
    // - 예: 337
    private int providerId;

    // 플랫폼 이름
    // - JSON 키: "provider_name"
    // - 예: "Disney+", "Netflix"
    private String providerName;

    // 플랫폼 로고 이미지 경로
    // - JSON 키: "logo_path"
    // - 예: "/abc123logo.png"
    private String logoPath;
}
