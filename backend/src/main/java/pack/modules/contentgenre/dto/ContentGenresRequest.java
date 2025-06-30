package pack.modules.contentgenre.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 콘텐츠-장르 매핑 등록 요청 DTO
 */
@Getter
@Setter
public class ContentGenresRequest {

    /** 콘텐츠 ID */
    private int contentId;

    /** 장르 ID */
    private int genreId;
}
