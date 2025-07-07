package pack.index.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * ContentsNetflixResponse DTO
 * ---------------------------------------------------------
 * ○ 넷플릭스 콘텐츠 목록 응답 전용 DTO
 * ○ index.html 넷플릭스 섹션에서 사용됨
 * ○ voteCount 제외 (평점만 출력)
 * ---------------------------------------------------------
 */
@Getter
@Setter
public class ContentsNetflixResponse {

    // 콘텐츠 고유 ID
    private Long contentId;

    // 콘텐츠 제목
    private String title;

    // 포스터 이미지 경로 (TMDB 상대경로)
    private String posterPath;

    // 개봉일 또는 방영일 (문자열로 전달)
    private String releaseDate;

    // TMDB 평점 (0.0 ~ 10.0)
    private Float rating;
}
