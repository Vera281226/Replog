package pack.modules.contents.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 콘텐츠 등록/수정 시 요청 데이터를 받는 DTO
 */
@Getter
@Setter
public class ContentsRequest {

    private Integer tmdbId;          // TMDB 콘텐츠 고유 ID (외부 연동용)
    private String title;            // 콘텐츠 제목
    private String overview;         // 콘텐츠 설명
    private Float rating;            // 평점
    private LocalDate releaseDate;   // 개봉일
    private String mediaType;        // movie 또는 tv
    private String posterPath;       // 포스터 이미지 경로
    private String backdropPath;     // 배경 이미지 경로
}
