package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

// ==========================================================
// TMDB 국가별 OTT 제공 정보 DTO
// - /movie/{id}/watch/providers 또는 /tv/{id}/watch/providers 응답 구조 중
//   특정 국가(KR 등)에 대한 지역 단위 제공자 목록을 매핑
// - 예시:
//   {
//     "KR": {
//       "flatrate": [
//         { "provider_id": 337, "provider_name": "Disney Plus", "logo_path": "/abc.png" }
//       ]
//     }
//   }
// ==========================================================
@Getter
@Setter
public class TmdbWatchProviderRegion {

    // 구독형(정액제) OTT 플랫폼 리스트
    // - JSON 키: "flatrate"
    // - 예: List<TmdbProviderDto>
    private List<TmdbProviderDto> flatrate;
}
