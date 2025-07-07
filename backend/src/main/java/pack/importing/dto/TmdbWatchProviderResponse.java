package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

// ===============================================================
// TMDB 플랫폼 전체 응답 DTO (콘텐츠 단위)
// - /movie/{id}/watch/providers 또는 /tv/{id}/watch/providers API 응답 매핑
// - 예시:
//   {
//     "id": 12345,
//     "results": {
//       "KR": {
//         "flatrate": [
//           { "provider_id": 337, "provider_name": "Disney Plus", "logo_path": "/abc.png" }
//         ]
//       }
//     }
//   }
// ===============================================================
@Getter
@Setter
public class TmdbWatchProviderResponse {

    // 콘텐츠 고유 ID (TMDB 기준)
    // - JSON 키: "id"
    // - 예: 12345
    private int id;

    // 국가별 플랫폼 정보
    // - JSON 키: "results"
    // - 예: Map<String, TmdbWatchProviderRegion>
    //   → "KR" → TmdbWatchProviderRegion 객체
    private Map<String, TmdbWatchProviderRegion> results;
}
