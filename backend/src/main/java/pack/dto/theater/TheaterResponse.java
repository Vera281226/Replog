package pack.dto.theater;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheaterResponse {
    private Integer id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}