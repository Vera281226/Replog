package pack.index.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pack.index.dto.IndexNowPlayingResponse;
import pack.index.dto.ContentsDisneyResponse;
import pack.index.dto.ContentsNetflixResponse;
import pack.index.dto.TrailerResponse;
import pack.index.dto.IndexHotReviewResponse; // ✅ 추가
import pack.index.service.IndexService;

import java.util.List;

/**
 * IndexController 클래스
 * ------------------------------------------------------------------
 * ○ index.html에서 사용하는 데이터들을 제공하는 REST API 컨트롤러
 * ○ URI prefix: /api/index/...
 * ------------------------------------------------------------------
 */
@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexController {

    // ----------------------------------------------------------
    // ✅ IndexService 의존성 주입
    // ----------------------------------------------------------
    private final IndexService indexService;

    // ----------------------------------------------------------
    // ✅ 현재 상영 중 영화 목록 API
    // [GET] /api/index/now-playing
    // ----------------------------------------------------------
    @GetMapping("/now-playing")
    public ResponseEntity<List<IndexNowPlayingResponse>> getNowPlaying() {
        return ResponseEntity.ok(indexService.getNowPlayingMovies());
    }

    // ----------------------------------------------------------
    // ✅ 디즈니+ 인기 콘텐츠 API
    // [GET] /api/index/disney
    // ----------------------------------------------------------
    @GetMapping("/disney")
    public ResponseEntity<List<ContentsDisneyResponse>> getDisneyContents() {
        return ResponseEntity.ok(indexService.getDisneyContents());
    }

    // ----------------------------------------------------------
    // ✅ 넷플릭스 인기 콘텐츠 API
    // [GET] /api/index/netflix
    // ----------------------------------------------------------
    @GetMapping("/netflix")
    public ResponseEntity<List<ContentsNetflixResponse>> getNetflixContents() {
        return ResponseEntity.ok(indexService.getNetflixContents());
    }


    // ----------------------------------------------------------
    // ✅ 추천 예고편 API
    // [GET] /api/index/trailers
    // ----------------------------------------------------------
    @GetMapping("/trailers")
    public ResponseEntity<List<TrailerResponse>> getRecommendedTrailers() {
        return ResponseEntity.ok(indexService.getRecommendedTrailers());
    }

    // ----------------------------------------------------------
    // ✅ 지금 뜨는 리뷰 API (좋아요 순 상위 10개)
    // [GET] /api/index/hot-reviews
    // ----------------------------------------------------------
    @GetMapping("/hot-reviews")
    public ResponseEntity<List<IndexHotReviewResponse>> getHotReviews() {
        return ResponseEntity.ok(indexService.getHotReviews());
    }
}
