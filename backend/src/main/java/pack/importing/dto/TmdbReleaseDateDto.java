package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbReleaseDateDto {
    private List<TmdbReleaseDateResult> results = new ArrayList<>();
    
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbReleaseDateResult {
        @JsonProperty("iso_3166_1")
        private String countryCode;
        
        @JsonProperty("release_dates")
        private List<TmdbReleaseInfo> releaseDates = new ArrayList<>();
    }
    
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbReleaseInfo {
        private String certification;
        
        @JsonProperty("release_date")
        private String releaseDate;
        
        private int type;  // 1: Premiere, 2: Theatrical (limited), 3: Theatrical, etc.
    }
}
