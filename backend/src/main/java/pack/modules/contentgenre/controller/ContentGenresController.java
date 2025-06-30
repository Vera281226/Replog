package pack.modules.contentgenre.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.modules.contentgenre.dto.ContentGenresRequest;
import pack.modules.contentgenre.dto.ContentGenresResponse;
import pack.modules.contentgenre.service.ContentGenresService;

import java.util.List;

/**
 * 콘텐츠-장르 매핑 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/content-genres")
@RequiredArgsConstructor
public class ContentGenresController {

    private final ContentGenresService contentGenresService;

    /**
     * 콘텐츠-장르 매핑 등록 API
     * @param request 매핑 요청 DTO
     * @return 201 Created 응답
     */
    @PostMapping
    public ResponseEntity<Void> createMapping(@RequestBody ContentGenresRequest request) {
        contentGenresService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 특정 콘텐츠에 대한 장르 목록 조회 API
     * @param contentId 콘텐츠 ID
     * @return 장르 응답 DTO 리스트
     */
    @GetMapping("/{contentId}")
    public ResponseEntity<List<ContentGenresResponse>> getGenresByContentId(@PathVariable int contentId) {
        List<ContentGenresResponse> result = contentGenresService.findByContentId(contentId);
        return ResponseEntity.ok(result);
    }

    /**
     * 콘텐츠-장르 매핑 삭제 API
     * @param contentId 콘텐츠 ID
     * @param genreId 장르 ID
     * @return 204 No Content 응답
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteMapping(@RequestParam int contentId, @RequestParam int genreId) {
        contentGenresService.delete(contentId, genreId);
        return ResponseEntity.noContent().build();
    }
}