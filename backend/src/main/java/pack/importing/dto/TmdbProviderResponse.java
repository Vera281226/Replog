package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * TMDB에서 가져온 Provider 목록 전체 응답 DTO
 */
@Getter
@Setter
public class TmdbProviderResponse {

    @JsonProperty("results")
    private List<TmdbProviderDto> results;
}
