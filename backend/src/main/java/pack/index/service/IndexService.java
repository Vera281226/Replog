package pack.index.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pack.index.dto.IndexNowPlayingResponse;
// import pack.index.dto.IndexHotReviewResponse;
// import pack.index.repository.IndexReviewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IndexService 클래스
 * - index.html에서 사용하는 데이터(현재 상영작, 핫 리뷰)를 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class IndexService {

    /** TMDB API 호출용 RestTemplate */
    private final RestTemplate restTemplate;

    // /** 리뷰 조회용 Repository */
    // private final IndexReviewRepository indexReviewRepository;

    /** TMDB API 키 (application.properties 또는 .yml에서 주입) */
    @Value("${tmdb.api.key}")
    private String apiKey;

    /**
     * 현재 상영 중 영화 목록 조회
     * - TMDB의 now_playing 엔드포인트 호출
     * - 한국 기준 최신 상영작 목록 반환
     *
     * @return List<IndexNowPlayingResponse>
     */
    public List<IndexNowPlayingResponse> getNowPlayingMovies() {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.themoviedb.org/3/movie/now_playing")
                .queryParam("language", "ko-KR")
                .queryParam("region", "KR")
                .queryParam("api_key", apiKey)
                .build()
                .toString();

        Map response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        List<IndexNowPlayingResponse> list = new ArrayList<>();
        for (Map<String, Object> item : results) {
            IndexNowPlayingResponse dto = new IndexNowPlayingResponse();
            dto.setTitle((String) item.get("title"));
            dto.setOverview((String) item.get("overview"));
            dto.setPosterPath((String) item.get("poster_path"));
            dto.setReleaseDate((String) item.get("release_date"));
            list.add(dto);
        }

        return list;
    }

    /*
    // 지금 뜨는 리뷰 목록 조회
    // - DB 기반 JPQL 쿼리를 통해 review, member, contents JOIN 조회
    public List<IndexHotReviewResponse> getHotReviews() {
        return indexReviewRepository.findHotReviews()
                .stream()
                .limit(10)
                .toList();
    }
    */
}
