package pack.modules.contents.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 클라이언트에게 콘텐츠 정보를 응답할 때 사용하는 DTO
 */
@Getter
@Setter
public class ContentsResponse {

    /* 내부 고유 콘텐츠 ID (PK) */
    private Integer contentId;

    /* TMDB 콘텐츠 고유 ID (외부 연동용) */
    private Integer tmdbId;

    /* 콘텐츠 제목 */
    private String title;

    /* 콘텐츠 설명 */
    private String overview;

    /* 평점 */
    private Float rating;

    /* 개봉일 */
    private LocalDate releaseDate;

    /* movie 또는 tv */
    private String mediaType;

    /* 포스터 경로 */
    private String posterPath;

    /* 배경 이미지 경로 */
    private String backdropPath;

    /* 생성 시각 */
    private LocalDateTime createdAt;

    /* 제공 플랫폼 이름 리스트 (예: ["Netflix", "Disney Plus"]) */
    private List<String> platform;

    /**
     * 전체 필드 초기화를 위한 생성자
     */
    public ContentsResponse(
            Integer contentId,
            Integer tmdbId,
            String title,
            String overview,
            Float rating,
            LocalDate releaseDate,
            String mediaType,
            String posterPath,
            String backdropPath,
            LocalDateTime createdAt,
            List<String> platform
    ) {
        this.contentId = contentId;
        this.tmdbId = tmdbId;
        this.title = title;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.mediaType = mediaType;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.createdAt = createdAt;
        this.platform = platform;
    }
}
