package pack.modules.contents.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pack.modules.contents.dto.ContentsRequest;
import pack.modules.contents.dto.ContentsResponse;
import pack.modules.contents.service.ContentsService;

/**
 * 콘텐츠 관련 HTTP 요청을 처리하는 컨트롤러 클래스입니다.
 * - 등록, 전체 조회, 단일 조회, 수정, 삭제 기능 제공
 */
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;

    /**
     * 콘텐츠 등록 API
     *
     * @param request 콘텐츠 요청 DTO (JSON 바디)
     * @return 생성된 콘텐츠의 ID
     */
    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody ContentsRequest request) {
        Integer id = contentsService.createContent(request);
        return ResponseEntity.ok(id);
    }

    /**
     * 전체 콘텐츠 목록 조회 API
     *
     * @return 콘텐츠 응답 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<List<ContentsResponse>> getAll() {
        List<ContentsResponse> list = contentsService.getAllContents();
        return ResponseEntity.ok(list);
    }

    /**
     * 단일 콘텐츠 조회 API
     *
     * @param id 콘텐츠 ID
     * @return 해당 콘텐츠 응답 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContentsResponse> getOne(@PathVariable Integer id) {
        ContentsResponse result = contentsService.getContentById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 콘텐츠 수정 API
     *
     * @param id 수정할 콘텐츠 ID
     * @param request 수정할 콘텐츠 요청 DTO
     * @return 수정된 콘텐츠의 ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Integer> update(@PathVariable Integer id, @RequestBody ContentsRequest request) {
        Integer updatedId = contentsService.updateContent(id, request);
        return ResponseEntity.ok(updatedId);
    }

    /**
     * 콘텐츠 삭제 API
     *
     * @param id 삭제할 콘텐츠 ID
     * @return 204 No Content 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        contentsService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}
