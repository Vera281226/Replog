package pack.search.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * SearchResponse DTO
 * - 검색 결과로 반환되는 콘텐츠 요약 정보
 * - contents 테이블 기반
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchResponse {  // ✅ 반드시 class로 유지

    private Integer contentId;       // 콘텐츠 고유 ID
    private String title;            // 콘텐츠 제목
    private String overview;         // 줄거리(개요)
    private String posterPath;       // 포스터 이미지 경로
    private LocalDate releaseDate;      // ✅ 반드시 String (DATE 매핑 오류 방지)
    private Float rating;           // 평점
    private String mediaType;        // 콘텐츠 유형 (movie 또는 tv)

    /**
     * Native Query 매핑용 생성자
     * - 쿼리 컬럼 순서와 타입 100% 일치해야 함
     */
    public SearchResponse(Integer contentId, String title, String overview,
                          String posterPath, LocalDate releaseDate,
                          Float rating, String mediaType) {
        this.contentId = contentId;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.mediaType = mediaType;
    }
}
