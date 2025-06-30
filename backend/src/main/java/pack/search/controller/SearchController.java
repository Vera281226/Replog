package pack.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pack.search.dto.SearchResponse;
import pack.search.service.SearchService;

import java.util.List;

/**
 * SearchController 클래스
 * - 콘텐츠 검색 기능을 제공하는 REST API 컨트롤러
 * - URL: /api/search?keyword=...
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    /** SearchService 의존성 주입 */
    private final SearchService searchService;

    /**
     * 키워드를 포함한 콘텐츠 검색 API
     * [GET] /api/search?keyword=...
     * @param keyword 검색 키워드
     * @return 검색된 콘텐츠 리스트
     */
    @GetMapping
    public ResponseEntity<List<SearchResponse>> search(@RequestParam("keyword") String keyword) {
        List<SearchResponse> result = searchService.searchContentsByKeyword(keyword);
        return ResponseEntity.ok(result);
    }
}