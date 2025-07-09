package pack.index.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * IndexNowPlayingResponse DTO 클래스
 * -------------------------------------------------------------
 * ✅ index.html의 "현재 상영 중인 영화" 섹션에서 사용하는 응답 구조
 * ✅ TMDB에서 가져온 영화 데이터 중 필요한 필드만 선별
 * ✅ 프론트엔드에 전달되는 JSON 응답용 구조
 * ✅ 플랫폼 로고 출력을 위한 providerIds 필드 추가됨
 * -------------------------------------------------------------
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IndexNowPlayingResponse {

    // 영화 제목
    // 예: "쿵푸팬더 4"
    private String title;

    // 줄거리
    // 예: "포가 평화를 지키기 위한 새로운 모험에 나선다..."
    private String overview;

    // 포스터 이미지 경로
    // 예: "/abcd1234poster.jpg"
    private String posterPath;

    // 개봉일 (YYYY-MM-DD)
    // 예: "2025-06-01"
    private String releaseDate;

    // 평점 (0.0 ~ 10.0)
    private Float rating;

    // 내부 콘텐츠 ID (DB의 contents 테이블 PK)
    private Long contentId;

    // 플랫폼 ID 목록 (넷플릭스: 8, 디즈니+: 337 등)
    // 예: [8], [337], 또는 [8, 337]
    private List<Integer> providerIds;
}
