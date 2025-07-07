package pack.importing.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * TmdbPersonDto
 * -------------------------------------------------------------
 * ✅ TMDB 출연진 / 제작진 정보를 매핑하는 DTO 클래스입니다.
 * ✅ 사용 API: /movie/{id}/credits (영화 전용)
 * ✅ 모든 필드는 snake_case → camelCase 자동 매핑 사용
 * ❌ 예능(tv) 및 /tv/{id}/credits API는 사용하지 않습니다.
 * -------------------------------------------------------------
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbPersonDto {

    private int id;
    private String name;
    private String character;
    private String department;
    private String knownForDepartment;
    private String profilePath;
}
