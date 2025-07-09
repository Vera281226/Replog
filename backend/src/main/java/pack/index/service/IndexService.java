package pack.index.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pack.config.TmdbProperties;
import pack.index.dto.ContentsDisneyResponse;
import pack.index.dto.ContentsNetflixResponse;
import pack.index.dto.IndexHotReviewResponse;
import pack.index.dto.IndexNowPlayingResponse;
import pack.index.dto.TrailerResponse;
import pack.index.repository.IndexReviewRepository;
import pack.modules.contents.model.Contents;
import pack.modules.contents.repository.ContentsRepository;
import pack.modules.contentprovider.repository.ContentProvidersRepository; // ✅ 수정된 import

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IndexService 클래스
 * -------------------------------------------------------------------
 * ○ index.html에서 사용하는 데이터(현재 상영작, 디즈니+, 넷플릭스,
 *    개봉 예정, 추천 예고편, 지금 뜨는 리뷰)를 조회하는 서비스
 * ○ TMDB API는 RestTemplate 사용, DB는 JPA Repository 사용
 * -------------------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
public class IndexService {

    // ✅ TMDB API 설정 정보 주입
    private final TmdbProperties tmdbProperties;

    // ✅ TMDB 호출용 RestTemplate
    private final RestTemplate restTemplate;

    // ✅ 콘텐츠 테이블 Repository
    private final ContentsRepository contentsRepository;

    // ✅ 콘텐츠별 플랫폼 조회 Repository (올바른 경로로 수정됨)
    private final ContentProvidersRepository contentProvidersRepository;

    // ✅ 리뷰 좋아요 기반 조회용 Repository
    private final IndexReviewRepository indexReviewRepository;

    /**
     * ✅ 현재 상영작 조회 (DB 기반, providerIds 포함)
     */
    public List<IndexNowPlayingResponse> getNowPlayingMovies() {
        List<Contents> contentsList = contentsRepository.findAllContentsForResponse();
        List<IndexNowPlayingResponse> result = new ArrayList<>();

        for (Contents c : contentsList) {
            // ✅ 콘텐츠별 플랫폼 ID 목록 조회 (로고용 platform 정보)
            List<Integer> providerIds = contentProvidersRepository.findProviderIdsByContentId(c.getContentId());

            IndexNowPlayingResponse dto = new IndexNowPlayingResponse();
            dto.setContentId(c.getContentId().longValue());
            dto.setTitle(c.getTitle());
            dto.setOverview(c.getOverview());
            dto.setPosterPath(c.getPosterPath());
            dto.setReleaseDate(c.getReleaseDate() != null ? c.getReleaseDate().toString() : null);
            dto.setRating(c.getRating());
            dto.setProviderIds(providerIds); // ✅ 플랫폼 ID 포함
            result.add(dto);
        }

        return result;
    }

    /**
     * ✅ 디즈니+ 콘텐츠 조회 (DB 기반)
     */
    public List<ContentsDisneyResponse> getDisneyContents() {
        List<Contents> contentsList = contentsRepository.findDisneyContents();
        List<ContentsDisneyResponse> result = new ArrayList<>();

        for (Contents c : contentsList) {
            ContentsDisneyResponse dto = new ContentsDisneyResponse();
            dto.setContentId(c.getContentId().longValue());
            dto.setTitle(c.getTitle());
            dto.setPosterPath(c.getPosterPath());
            dto.setReleaseDate(c.getReleaseDate() != null ? c.getReleaseDate().toString() : null);
            dto.setRating(c.getRating());
            result.add(dto);
        }

        return result;
    }

    /**
     * ✅ 넷플릭스 콘텐츠 조회 (DB 기반)
     */
    public List<ContentsNetflixResponse> getNetflixContents() {
        List<Contents> contentsList = contentsRepository.findNetflixContents();
        List<ContentsNetflixResponse> result = new ArrayList<>();

        for (Contents c : contentsList) {
            ContentsNetflixResponse dto = new ContentsNetflixResponse();
            dto.setContentId(c.getContentId().longValue());
            dto.setTitle(c.getTitle());
            dto.setPosterPath(c.getPosterPath());
            dto.setReleaseDate(c.getReleaseDate() != null ? c.getReleaseDate().toString() : null);
            dto.setRating(c.getRating());
            result.add(dto);
        }

        return result;
    }

    /**
     * ✅ 추천 예고편 조회 (TMDB 실시간 호출)
     */
    public List<TrailerResponse> getRecommendedTrailers() {
        List<TrailerResponse> result = new ArrayList<>();
        String apiKey = tmdbProperties.getApiKey();
        String baseUrl = tmdbProperties.getBaseUrl();

        String popularUrl = baseUrl + "/movie/popular?language=ko-KR&page=1&api_key=" + apiKey;
        Map<String, Object> response = restTemplate.getForObject(popularUrl, Map.class);

        if (response != null && response.get("results") instanceof List) {
            List<Map<String, Object>> popularList = (List<Map<String, Object>>) response.get("results");

            for (Map<String, Object> item : popularList) {
                Integer tmdbId = (Integer) item.get("id");
                String title = (String) item.get("title");
                String overview = (String) item.get("overview");
                String posterPath = (String) item.get("poster_path");

                String videoUrl = baseUrl + "/movie/" + tmdbId + "/videos?api_key=" + apiKey;
                Map<String, Object> videoResponse = restTemplate.getForObject(videoUrl, Map.class);

                if (videoResponse != null && videoResponse.get("results") instanceof List) {
                    List<Map<String, Object>> videos = (List<Map<String, Object>>) videoResponse.get("results");

                    for (Map<String, Object> video : videos) {
                        String site = (String) video.get("site");
                        String type = (String) video.get("type");
                        String key = (String) video.get("key");

                        if ("YouTube".equals(site) && "Trailer".equals(type)) {
                            TrailerResponse trailer = new TrailerResponse();
                            trailer.setContentId(Long.valueOf(tmdbId));
                            trailer.setTitle(title);
                            trailer.setOverview(overview);
                            trailer.setPosterPath(posterPath);
                            trailer.setYoutubeKey(key);
                            result.add(trailer);
                            break;
                        }
                    }
                }

                if (result.size() >= 10) break;
            }
        }

        return result;
    }

    /**
     * ✅ 지금 뜨는 리뷰 조회 (좋아요 순 내림차순, 상위 10개)
     */
    public List<IndexHotReviewResponse> getHotReviews() {
        return indexReviewRepository.findTopReviews(PageRequest.of(0, 10));
    }
}
