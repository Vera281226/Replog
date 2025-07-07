package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * TmdbContentResponse
 * -------------------------------------------------------------
 * ○ TMDB 콘텐츠 목록 전체 응답을 매핑하는 DTO 클래스입니다.
 * ○ TMDB API의 "results" 배열만 사용하며, 다른 필드는 무시합니다.
 * ○ DTO 내부에 TmdbContentDto 리스트로 콘텐츠 배열 매핑
 * -------------------------------------------------------------
 */
@Getter
@Setter
public class TmdbContentResponse {

    // TMDB 콘텐츠 리스트
    // - JSON 키: "results"
    // - 각 항목은 TmdbContentDto로 자동 매핑됨
    private List<TmdbContentDto> results;
}
