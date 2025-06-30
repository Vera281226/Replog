package pack.modules.genres.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 장르 등록/수정 요청 데이터를 담는 DTO 클래스
 * - 클라이언트가 보내는 JSON 데이터를 처리하기 위한 구조
 */
@Getter
@Setter
public class GenresRequest {

    /** 장르 ID (TMDB에서 제공하는 고유 ID) */
    private Integer genreId;

    /** 장르 이름 */
    private String name;
}
