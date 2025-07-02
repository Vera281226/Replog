package pack.modules.contents.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.modules.contents.dto.ContentsRequest;
import pack.modules.contents.dto.ContentsResponse;
import pack.modules.contents.service.ContentsService;

import java.util.List;

/* 콘텐츠 관련 HTTP 요청을 처리하는 컨트롤러 클래스 */
/* - 등록, 필터 포함 전체 조회, 단일 조회, 수정, 삭제 기능 제공 */
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;

    /* 콘텐츠 등록 API */
    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody ContentsRequest request) {
        Integer id = contentsService.createContent(request);
        return ResponseEntity.ok(id);
    }

    /* 전체 콘텐츠 목록 조회 API (필터 조건 포함) */
    @GetMapping
    public ResponseEntity<List<ContentsResponse>> getAllWithFilters(
        @RequestParam(name = "sort",          required = false) String sort,
        @RequestParam(name = "releaseStart",  required = false) String releaseStart,
        @RequestParam(name = "releaseEnd",    required = false) String releaseEnd,
        @RequestParam(name = "genres",        required = false) List<String> genres,
        @RequestParam(name = "age",           required = false) String age,
        @RequestParam(name = "ratingMin",     required = false) Float ratingMin,
        @RequestParam(name = "ratingMax",     required = false) Float ratingMax,
        @RequestParam(name = "runtimeMin",    required = false) Integer runtimeMin,
        @RequestParam(name = "runtimeMax",    required = false) Integer runtimeMax,
        @RequestParam(name = "platforms",     required = false) List<String> platforms
    ) {
        List<ContentsResponse> list = contentsService.getFilteredContents(
                sort, releaseStart, releaseEnd, genres, age,
                ratingMin, ratingMax, runtimeMin, runtimeMax, platforms
        );
        return ResponseEntity.ok(list);
    }

    /* 단일 콘텐츠 조회 API */
    @GetMapping("/{id}")
    public ResponseEntity<ContentsResponse> getOne(@PathVariable("id") Integer id) {
        ContentsResponse result = contentsService.getContentById(id);
        return ResponseEntity.ok(result);
    }

    /* 콘텐츠 수정 API */
    @PutMapping("/{id}")
    public ResponseEntity<Integer> update(@PathVariable("id") Integer id, @RequestBody ContentsRequest request) {
        Integer updatedId = contentsService.updateContent(id, request);
        return ResponseEntity.ok(updatedId);
    }

    /* 콘텐츠 삭제 API */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contentsService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}
