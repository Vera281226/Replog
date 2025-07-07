package pack.index.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pack.index.dto.IndexNowPlayingResponse;
import pack.index.dto.ContentsDisneyResponse;
import pack.index.dto.ContentsNetflixResponse;
import pack.index.dto.UpcomingResponse;
import pack.index.dto.TrailerResponse;
import pack.index.dto.IndexUpcomingMixResponse;
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
    // - 현재 상영작, 디즈니+, 넷플릭스, 개봉 예정 등 서비스 호출
    // ----------------------------------------------------------
    private final IndexService indexService;

    // ----------------------------------------------------------
    // ✅ 현재 상영 중 영화 목록 API
    // [GET] /api/index/now-playing
    // - 개봉일 내림차순으로 영화 목록 반환
    // ----------------------------------------------------------
    @GetMapping("/now-playing")
    public ResponseEntity<List<IndexNowPlayingResponse>> getNowPlaying() {
        return ResponseEntity.ok(indexService.getNowPlayingMovies());
    }

    // ----------------------------------------------------------
    // ✅ 디즈니+ 인기 콘텐츠 API
    // [GET] /api/index/disney
    // - 플랫폼명이 'Disney Plus'인 콘텐츠만 조회
    // ----------------------------------------------------------
    @GetMapping("/disney")
    public ResponseEntity<List<ContentsDisneyResponse>> getDisneyContents() {
        return ResponseEntity.ok(indexService.getDisneyContents());
    }

    // ----------------------------------------------------------
    // ✅ 넷플릭스 인기 콘텐츠 API
    // [GET] /api/index/netflix
    // - 플랫폼명이 'Netflix'인 콘텐츠만 조회
    // ----------------------------------------------------------
    @GetMapping("/netflix")
    public ResponseEntity<List<ContentsNetflixResponse>> getNetflixContents() {
        return ResponseEntity.ok(indexService.getNetflixContents());
    }

    // ----------------------------------------------------------
    // ✅ 개봉 예정 콘텐츠 API (DB 기반)
    // [GET] /api/index/upcoming
    // - 오늘 이후 release_date 기준 정렬
    // ----------------------------------------------------------
    @GetMapping("/upcoming")
    public ResponseEntity<List<UpcomingResponse>> getUpcomingContents() {
        return ResponseEntity.ok(indexService.getUpcomingContents());
    }

    // ----------------------------------------------------------
    // ✅ 추천 예고편 API (TMDB 실시간 호출)
    // [GET] /api/index/trailers
    // - TMDB 인기 콘텐츠 중 유튜브 예고편 포함된 10개 추출
    // ----------------------------------------------------------
    @GetMapping("/trailers")
    public ResponseEntity<List<TrailerResponse>> getRecommendedTrailers() {
        return ResponseEntity.ok(indexService.getRecommendedTrailers());
    }

    // ----------------------------------------------------------
    // ✅ 콘텐츠 예정작 (넷플릭스/디즈니+ 실시간 섞기)
    // [GET] /api/index/upcoming-mix
    // - TMDB 실시간 호출 후 6개 랜덤 반환
    // ----------------------------------------------------------
    @GetMapping("/upcoming-mix")
    public ResponseEntity<List<IndexUpcomingMixResponse>> getUpcomingMixedContents() {
        return ResponseEntity.ok(indexService.getUpcomingMixedContents());
    }

    /*
    // 🔒 지금 뜨는 리뷰 API (향후 구현 예정)
    // [GET] /api/index/hot-reviews
    @GetMapping("/hot-reviews")
    public ResponseEntity<List<IndexHotReviewResponse>> getHotReviews() {
        return ResponseEntity.ok(indexService.getHotReviews());
    }
    */
}
