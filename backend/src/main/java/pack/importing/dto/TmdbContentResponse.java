package pack.importing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * TMDB API에서 수신하는 영화 콘텐츠 전체 응답 DTO
 */
@Getter
@Setter
public class TmdbContentResponse {

    /** 콘텐츠 목록 */
    private List<TmdbContentDto> results;
}
