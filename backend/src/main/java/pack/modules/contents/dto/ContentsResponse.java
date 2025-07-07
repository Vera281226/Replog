package pack.modules.contents.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ContentsResponse
 * ------------------------------------------------------------------
 * ○ 클라이언트에게 콘텐츠 정보를 응답할 때 사용하는 DTO 클래스
 * ○ Entity → JSON 변환 구조이며, DB 저장은 Contents 엔티티 사용
 * ------------------------------------------------------------------
 */
@Getter
@Setter
public class ContentsResponse {

    // 콘텐츠 고유 ID (내부용 PK)
    private Integer contentId;

    // TMDB 고유 콘텐츠 ID (외부 연동용)
    private Integer tmdbId;

    // 콘텐츠 제목
    private String title;

    // 콘텐츠 설명 (줄거리)
    private String overview;

    // TMDB 평점
    private Float rating;

    // 개봉일 (YYYY-MM-DD)
    private LocalDate releaseDate;

    // 미디어 타입 ("movie", "tv")
    private String mediaType;

    // 포스터 이미지 경로
    private String posterPath;

    // 배경 이미지 경로
    private String backdropPath;

    // 러닝타임 (분 단위)
    private Integer runtime;

    // 연령 등급 (예: "전체관람가", "15세", "18세")
    private String ageRating;

    // 콘텐츠 등록 일시
    private LocalDateTime createdAt;

    // 플랫폼 이름 리스트 (예: ["Netflix", "Disney Plus"])
    private List<String> platforms;

    // 장르 이름 리스트 (예: ["액션", "스릴러"])
    private List<String> genres;

    // ✅ 모든 필드를 포함한 생성자
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
            Integer runtime,
            String ageRating,
            LocalDateTime createdAt,
            List<String> platforms,
            List<String> genres
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
        this.runtime = runtime;
        this.ageRating = ageRating;
        this.createdAt = createdAt;
        this.platforms = platforms;
        this.genres = genres;
    }
}
