package pack.index.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pack.config.TmdbProperties;
import pack.index.dto.ContentsDisneyResponse;
import pack.index.dto.ContentsNetflixResponse;
import pack.index.dto.IndexNowPlayingResponse;
import pack.index.dto.IndexUpcomingMixResponse;
import pack.index.dto.TrailerResponse;
import pack.index.dto.UpcomingResponse;
import pack.modules.contents.model.Contents;
import pack.modules.contents.repository.ContentsRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * IndexService 클래스
 * -------------------------------------------------------------------
 * ○ index.html에서 사용하는 데이터(현재 상영작, 디즈니+, 넷플릭스,
 *    개봉 예정, 추천 예고편, 콘텐츠 예정작)를 DB 또는 TMDB API에서 조회
 * ○ TMDB API 키는 TmdbProperties 통해 주입
 * -------------------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
public class IndexService {

    // -------------------------------------------------------------------
    // ✅ TMDB 설정 주입 (baseUrl, apiKey 등)
    // -------------------------------------------------------------------
    private final TmdbProperties tmdbProperties;

    // -------------------------------------------------------------------
    // ✅ RestTemplate (TMDB API 호출용)
    // -------------------------------------------------------------------
    private final RestTemplate restTemplate;

    // -------------------------------------------------------------------
    // ✅ DB 조회용 Repository
    // -------------------------------------------------------------------
    private final ContentsRepository contentsRepository;

    // -------------------------------------------------------------------
    // ✅ 현재 상영작 조회 (DB 기반)
    // -------------------------------------------------------------------
    public List<IndexNowPlayingResponse> getNowPlayingMovies() {
        List<Contents> contentsList = contentsRepository.findAllContentsForResponse();
        List<IndexNowPlayingResponse> result = new ArrayList<>();

        for (Contents c : contentsList) {
            IndexNowPlayingResponse dto = new IndexNowPlayingResponse();
            dto.setContentId(c.getContentId().longValue());
            dto.setTitle(c.getTitle());
            dto.setOverview(c.getOverview());
            dto.setPosterPath(c.getPosterPath());
            dto.setReleaseDate(c.getReleaseDate() != null ? c.getReleaseDate().toString() : null);
            dto.setRating(c.getRating());
            result.add(dto);
        }

        return result;
    }

    // -------------------------------------------------------------------
    // ✅ 디즈니+ 콘텐츠 조회 (DB 기반)
    // -------------------------------------------------------------------
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

    // -------------------------------------------------------------------
    // ✅ 넷플릭스 콘텐츠 조회 (DB 기반)
    // -------------------------------------------------------------------
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

    // -------------------------------------------------------------------
    // ✅ 개봉 예정 콘텐츠 조회 (DB 기반)
    // -------------------------------------------------------------------
    public List<UpcomingResponse> getUpcomingContents() {
        List<Contents> contentsList = contentsRepository.findUpcomingContents();
        List<UpcomingResponse> result = new ArrayList<>();

        for (Contents c : contentsList) {
            UpcomingResponse dto = new UpcomingResponse();
            dto.setContentId(c.getContentId().longValue());
            dto.setTitle(c.getTitle());
            dto.setPosterPath(c.getPosterPath());
            dto.setReleaseDate(c.getReleaseDate() != null ? c.getReleaseDate().toString() : null);
            dto.setRating(c.getRating());
            result.add(dto);
        }

        return result;
    }

    // -------------------------------------------------------------------
    // ✅ 추천 예고편 조회 (TMDB 실시간 호출)
    // -------------------------------------------------------------------
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

    // -------------------------------------------------------------------
    // ✅ 콘텐츠 예정작 조회 (넷플릭스 + 디즈니+, TMDB 실시간 호출)
    // - primary_release_date.gte + region=KR + monetization=flatrate
    // -------------------------------------------------------------------
    public List<IndexUpcomingMixResponse> getUpcomingMixedContents() {
        String apiKey = tmdbProperties.getApiKey();
        String baseUrl = tmdbProperties.getBaseUrl();
        String today = "2025-06-10"; // ✅ 테스트 날짜 (지금은 고정된 날짜로 지정)

        String netflixUrl = baseUrl + "/discover/movie"
                + "?with_watch_providers=8"
                + "&watch_region=KR"
                + "&region=KR"
                + "&language=ko-KR"
                + "&sort_by=primary_release_date.asc"
                + "&with_watch_monetization_types=flatrate"
                + "&include_adult=false"
                + "&include_video=false"
                + "&primary_release_date.gte=" + today
                + "&page=1"
                + "&api_key=" + apiKey;

        String disneyUrl = baseUrl + "/discover/movie"
                + "?with_watch_providers=337"
                + "&watch_region=KR"
                + "&region=KR"
                + "&language=ko-KR"
                + "&sort_by=primary_release_date.asc"
                + "&with_watch_monetization_types=flatrate"
                + "&include_adult=false"
                + "&include_video=false"
                + "&primary_release_date.gte=" + today
                + "&page=1"
                + "&api_key=" + apiKey;

        List<IndexUpcomingMixResponse> netflixList = fetchUpcomingFromTmdb(netflixUrl, "netflix");
        List<IndexUpcomingMixResponse> disneyList = fetchUpcomingFromTmdb(disneyUrl, "disney");

        List<IndexUpcomingMixResponse> combined = new ArrayList<>();
        combined.addAll(netflixList);
        combined.addAll(disneyList);
        Collections.shuffle(combined);

        return combined.stream()
                .limit(6)
                .toList();
    }

    // -------------------------------------------------------------------
    // ✅ TMDB 응답을 IndexUpcomingMixResponse 리스트로 변환
    // -------------------------------------------------------------------
    private List<IndexUpcomingMixResponse> fetchUpcomingFromTmdb(String url, String platform) {
        List<IndexUpcomingMixResponse> list = new ArrayList<>();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.get("results") instanceof List) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("results");

            for (Map<String, Object> item : items) {
                Integer id = (Integer) item.get("id");
                String title = (String) item.get("title");
                String posterPath = (String) item.get("poster_path");
                String releaseDate = (String) item.get("release_date");
                String mediaType = "movie";
                String backdropPath = (String) item.get("backdrop_path");

                IndexUpcomingMixResponse dto = new IndexUpcomingMixResponse();
                dto.setContentId(id);
                dto.setTitle(title);
                dto.setPosterPath(posterPath);
                dto.setReleaseDate(releaseDate);
                dto.setPlatform(platform);
                dto.setMediaType(mediaType);
                dto.setBackdropPath(backdropPath);

                list.add(dto);
            }
        }

        return list;
    }

    // -------------------------------------------------------------------
    // 🔒 지금 뜨는 리뷰 API (향후 구현 예정)
    // public List<IndexHotReviewResponse> getHotReviews() {
    //     return ...
    // }
    // -------------------------------------------------------------------
}
