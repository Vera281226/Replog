package pack.modules.contentprovider.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.modules.contentprovider.dto.ContentProvidersRequest;
import pack.modules.contentprovider.dto.ContentProvidersResponse;
import pack.modules.contentprovider.service.ContentProvidersService;

import java.util.List;

/**
 * 콘텐츠-플랫폼 매핑 컨트롤러
 * <p>
 * - 콘텐츠와 플랫폼 간 다대다 매핑 CRUD API 제공
 */
@RestController
@RequestMapping("/api/content-providers")
@RequiredArgsConstructor
public class ContentProvidersController {

    private final ContentProvidersService service;

    /**
     * 콘텐츠-플랫폼 매핑 저장
     *
     * @param request 요청 DTO
     * @return 201 Created
     */
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ContentProvidersRequest request) {
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 전체 콘텐츠-플랫폼 매핑 목록 조회
     *
     * @return 응답 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<List<ContentProvidersResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * 단일 콘텐츠-플랫폼 매핑 조회
     *
     * @param contentId  콘텐츠 ID
     * @param providerId 플랫폼 ID
     * @return 응답 DTO
     */
    @GetMapping("/{contentId}/{providerId}")
    public ResponseEntity<ContentProvidersResponse> findById(
            @PathVariable int contentId,
            @PathVariable int providerId
    ) {
        return ResponseEntity.ok(service.findById(contentId, providerId));
    }

    /**
     * 콘텐츠-플랫폼 매핑 삭제
     *
     * @param contentId  콘텐츠 ID
     * @param providerId 플랫폼 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{contentId}/{providerId}")
    public ResponseEntity<Void> delete(
            @PathVariable int contentId,
            @PathVariable int providerId
    ) {
        service.delete(contentId, providerId);
        return ResponseEntity.noContent().build();
    }
}
