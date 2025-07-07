package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// ==========================================================
// TMDB 플랫폼 목록 응답 DTO
// - /watch/providers/movie 또는 /watch/providers/tv 응답 전체 구조 매핑
// - 내부에 OTT 제공자 리스트 포함 (TmdbProviderDto)
// - 예시:
//   {
//     "results": [
//       {
//         "provider_id": 337,
//         "provider_name": "Disney Plus",
//         "logo_path": "/xyz.png"
//       },
//       ...
//     ]
//   }
// ==========================================================
@Getter
@Setter
public class TmdbProviderResponse {

    // OTT 제공자 리스트
    // - JSON 키: "results"
    // - 예: List<TmdbProviderDto>
    private List<TmdbProviderDto> results;
}
