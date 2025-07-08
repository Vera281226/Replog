// TmdbTvContentRatingDto.java (신규 파일)
package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbTvContentRatingDto {
    private List<TvRatingResult> results = new ArrayList<>();

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TvRatingResult {
        private String iso_3166_1;
        private String rating;
    }

    // 국가 코드별 certification 반환
    public String getCertificationByCountry(String country) {
        return results.stream()
            .filter(r -> country.equals(r.getIso_3166_1()))
            .map(TvRatingResult::getRating)
            .filter(r -> r != null && !r.isEmpty())
            .findFirst().orElse(null);
    }

    // 첫 번째 certification 반환
    public String getFirstCertification() {
        return results.stream()
            .map(TvRatingResult::getRating)
            .filter(r -> r != null && !r.isEmpty())
            .findFirst().orElse(null);
    }
}
