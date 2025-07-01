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
public class SearchResponse {

    private Integer contentId;       // 콘텐츠 고유 ID
    private String title;            // 콘텐츠 제목
    private String overview;         // 줄거리(개요)
    private String posterPath;       // 포스터 이미지 경로
    private LocalDate releaseDate;   // 개봉일자 or 첫 방영일 (LocalDate 타입)
    private Float rating;            // TMDB 평균 평점
    private String mediaType;        // 콘텐츠 유형 ('movie' or 'tv')

    /**
     * JPQL 생성자 방식 DTO 매핑용 생성자
     * - 쿼리에서 new 키워드로 이 생성자를 직접 호출함
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
