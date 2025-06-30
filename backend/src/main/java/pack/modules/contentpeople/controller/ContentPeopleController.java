package pack.modules.contentpeople.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.modules.contentpeople.dto.ContentPeopleRequest;
import pack.modules.contentpeople.dto.ContentPeopleResponse;
import pack.modules.contentpeople.service.ContentPeopleService;

import java.util.List;

/**
 * 콘텐츠-인물 매핑 API 컨트롤러 클래스입니다.
 * 개선된 RESTful URL 구조 적용 버전
 */
@RestController
@RequestMapping("/api/content-people")
@RequiredArgsConstructor
public class ContentPeopleController {

    private final ContentPeopleService contentPeopleService;

    /**
     * 매핑 등록
     */
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ContentPeopleRequest request) {
        contentPeopleService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 콘텐츠 ID로 매핑 목록 조회
     * 예: GET /api/content-people/content/101
     */
    @GetMapping("/content/{contentId}")
    public ResponseEntity<List<ContentPeopleResponse>> findByContentId(@PathVariable int contentId) {
        List<ContentPeopleResponse> result = contentPeopleService.findByContentId(contentId);
        return ResponseEntity.ok(result);
    }

    /**
     * 단일 매핑 조회 (복합키 기반)
     * 예: GET /api/content-people/101/55/actor
     */
    @GetMapping("/{contentId}/{personId}/{role}")
    public ResponseEntity<ContentPeopleResponse> findOne(@PathVariable int contentId,
                                                         @PathVariable int personId,
                                                         @PathVariable String role) {
        ContentPeopleResponse result = contentPeopleService.findOne(contentId, personId, role);
        return ResponseEntity.ok(result);
    }

    /**
     * 캐릭터 이름 수정
     */
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody ContentPeopleRequest request) {
        contentPeopleService.update(request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 매핑 삭제
     * 예: DELETE /api/content-people/101/55/actor
     */
    @DeleteMapping("/{contentId}/{personId}/{role}")
    public ResponseEntity<Void> delete(@PathVariable int contentId,
                                       @PathVariable int personId,
                                       @PathVariable String role) {
        contentPeopleService.delete(contentId, personId, role);
        return ResponseEntity.noContent().build();
    }
}
