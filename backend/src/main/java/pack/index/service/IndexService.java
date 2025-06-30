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
 * - index 관련 기능(홈화면용 데이터)을 처리하는 비즈니스 로직 클래스
 * - TMDB API 또는 DB로부터 데이터를 받아와 가공 후 DTO 리스트로 반환
 */
@Service
@RequiredArgsConstructor
public class IndexService {

    /** RestTemplate 의존성 주입 (TMDB API 호출용) */
    private final RestTemplate restTemplate;

    // /** Repository 의존성 주입 (DB 리뷰 조회용) */
    // private final IndexReviewRepository indexReviewRepository;

    /** TMDB API 키 (application.properties 또는 .yml에서 주입) */
    @Value("${tmdb.api.key}")
    private String apiKey;

    /**
     * 현재 상영 중 영화 리스트를 TMDB API로부터 가져와서 가공 후 반환
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
    // 지금 뜨는 리뷰 리스트를 DB에서 조회 후 응답 DTO로 가공
    public List<IndexHotReviewResponse> getHotReviews() {
        return indexReviewRepository.findHotReviews();
    }
    */
}