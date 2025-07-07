package pack.index.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * IndexNowPlayingResponse DTO 클래스
 * - index.html의 "현재 상영 중인 영화" 섹션에서 사용하는 데이터 구조
 * - TMDB에서 가져온 영화 데이터 중 필요한 필드만 선별하여 정의
 * - 클라이언트에 응답할 JSON 형식 데이터로 자동 변환됨 (REST API 응답용)
 *
 *  TMDB API 연동 기반 (내부 DB 연동 아님)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IndexNowPlayingResponse {

    /**
     * 영화 제목
     * 예: "쿵푸팬더 4"
     */
    private String title;

    /**
     * 영화 설명 (줄거리)
     * 예: "포가 평화를 지키기 위한 새로운 모험에 나선다..."
     */
    private String overview;

    /**
     * 포스터 이미지 경로 (TMDB 기준)
     * 예: "/abcd1234poster.jpg"
     */
    private String posterPath;

    /**
     * 개봉일 (YYYY-MM-DD 형식)
     * 예: "2025-06-01"
     */
    private String releaseDate;

    private Float rating;
}
