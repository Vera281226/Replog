package pack.modules.contents.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * ContentsRequest
 * -----------------------------------------------------------------
 * ○ 클라이언트가 콘텐츠를 등록하거나 수정할 때 사용하는 요청 DTO
 * ○ JSON → DTO 매핑을 통해 Controller → Service 계층으로 전달됨
 * -----------------------------------------------------------------
 */
@Getter
@Setter
public class ContentsRequest {

    // TMDB 콘텐츠 고유 ID (외부 연동용)
    private Integer tmdbId;

    // 콘텐츠 제목
    private String title;

    // 콘텐츠 설명 (줄거리 요약)
    private String overview;

    // TMDB 기준 평점 (예: 8.5)
    private Float rating;

    // 개봉일 또는 첫 방영일 (예: 2025-01-01)
    private LocalDate releaseDate;

    // 콘텐츠 유형 (예: "movie", "tv")
    private String mediaType;

    // 포스터 이미지 경로 (/poster.jpg)
    private String posterPath;

    // 배경 이미지 경로 (/backdrop.jpg)
    private String backdropPath;

    // 러닝타임 (단위: 분)
    private Integer runtime;

    // 연령 등급 (예: 전체관람가, 15세 관람가 등)
    private String ageRating;

    // 플랫폼 이름 리스트 (예: ["Netflix", "Disney Plus"])
    private List<String> platforms;

    // 장르 이름 리스트 (예: ["액션", "드라마"])
    private List<String> genres;
}
