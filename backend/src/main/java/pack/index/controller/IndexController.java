package pack.index.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pack.index.dto.IndexNowPlayingResponse;
// import pack.index.dto.IndexHotReviewResponse; // 🔒 리뷰 기능 주석 처리
import pack.index.service.IndexService;

import java.util.List;

/**
 * IndexController 클래스
 * - index.html에서 사용하는 데이터를 제공하는 REST API 컨트롤러
 * - URL: /api/index/...
 */
@RestController
@RequestMapping("/api/index")
@RequiredArgsConstructor
public class IndexController {

    /** IndexService 의존성 주입 */
    private final IndexService indexService;

    /**
     * 현재 상영 중 영화 목록 조회 API
     * [GET] /api/index/now-playing
     * @return ResponseEntity<List<IndexNowPlayingResponse>>
     */
    @GetMapping("/now-playing")
    public ResponseEntity<List<IndexNowPlayingResponse>> getNowPlaying() {
        List<IndexNowPlayingResponse> result = indexService.getNowPlayingMovies();
        return ResponseEntity.ok(result);
    }

    /*
    // 🔒 지금 뜨는 리뷰 목록 조회 API (리뷰 담당자 연동 후 주석 해제 예정)
    @GetMapping("/hot-reviews")
    public ResponseEntity<List<IndexHotReviewResponse>> getHotReviews() {
        List<IndexHotReviewResponse> result = indexService.getHotReviews();
        return ResponseEntity.ok(result);
    }
    */
}
