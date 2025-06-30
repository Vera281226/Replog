package pack.modules.contentgenre.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 콘텐츠-장르 매핑 응답 DTO
 */
@Getter
@Setter
public class ContentGenresResponse {

    /** 콘텐츠 ID */
    private int contentId;

    /** 장르 ID */
    private int genreId;

    /** 장르 이름 */
    private String genreName;
}
