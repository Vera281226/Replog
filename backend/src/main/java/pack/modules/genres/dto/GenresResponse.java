package pack.modules.genres.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 장르 조회 응답 데이터를 담는 DTO 클래스
 */
@Getter
@Setter
public class GenresResponse {

    /** 장르 고유 ID */
    private Integer genreId;

    /** 장르 이름 */
    private String name;

    /** 생성 시각 */
    private LocalDateTime createdAt;
}
