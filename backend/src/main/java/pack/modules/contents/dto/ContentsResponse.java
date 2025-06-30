package pack.modules.contents.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 클라이언트에게 콘텐츠 정보를 응답할 때 사용하는 DTO
 */
@Getter
@Setter
public class ContentsResponse {

    private Integer contentId;           // 내부 고유 콘텐츠 ID (PK)
    private Integer tmdbId;              // TMDB 콘텐츠 고유 ID (외부 연동용)
    private String title;                // 콘텐츠 제목
    private String overview;             // 콘텐츠 설명
    private Float rating;                // 평점
    private LocalDate releaseDate;       // 개봉일
    private String mediaType;            // movie 또는 tv
    private String posterPath;           // 포스터 경로
    private String backdropPath;         // 배경 이미지 경로
    private LocalDateTime createdAt;     // 생성 시각
}
