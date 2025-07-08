package pack.modules.contents.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pack.modules.contents.dto.ContentsRequest;
import pack.modules.contents.dto.ContentsResponse;
import pack.modules.contents.service.ContentsService;

/**
 * ContentsController 클래스
 *
 * ○ 콘텐츠 관련 HTTP 요청 처리 컨트롤러
 * ○ 등록, 필터 포함 조회, 단일 조회, 수정, 삭제 기능 제공
 */
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentsController {

    // 서비스 계층 의존성 주입
    private final ContentsService contentsService;

    /**
     * 콘텐츠 등록 API
     * [POST] /api/contents
     * @param request 콘텐츠 등록 요청 DTO
     * @return 등록된 콘텐츠 ID
     */
    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody ContentsRequest request) {
        Integer id = contentsService.createContent(request);
        return ResponseEntity.ok(id);
    }

    /**
     * 콘텐츠 전체 조회 API (필터 조건 포함)
     * [GET] /api/contents
     *
     * @param sort 정렬 기준 (popularity, oldest, latest)
     * @param releaseStart 개봉일 시작
     * @param releaseEnd 개봉일 끝
     * @param genres 장르 필터 (추후 확장)
     * @param age 연령 등급 (예: "12세이상관람가")
     * @param ratingMin 최소 평점
     * @param ratingMax 최대 평점
     * @param runtimeMin 최소 러닝타임 (분)
     * @param runtimeMax 최대 러닝타임 (분)
     * @param platforms 제공 플랫폼 필터 (예: "Netflix", "Disney Plus")
     * @return 조건에 맞는 콘텐츠 응답 리스트
     */
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

    /**
     * 콘텐츠 단일 조회 API
     * [GET] /api/contents/{id}
     *
     * @param id 콘텐츠 고유 ID
     * @return 콘텐츠 상세 응답 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContentsResponse> getOne(@PathVariable("id") Integer id) {
        ContentsResponse result = contentsService.getContentById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 콘텐츠 수정 API
     * [PUT] /api/contents/{id}
     *
     * @param id 수정할 콘텐츠 ID
     * @param request 수정 내용 DTO
     * @return 수정된 콘텐츠 ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Integer> update(@PathVariable("id") Integer id, @RequestBody ContentsRequest request) {
        Integer updatedId = contentsService.updateContent(id, request);
        return ResponseEntity.ok(updatedId);
    }

    /**
     * 콘텐츠 삭제 API
     * [DELETE] /api/contents/{id}
     *
     * @param id 삭제할 콘텐츠 ID
     * @return 응답 없음 (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contentsService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

}
