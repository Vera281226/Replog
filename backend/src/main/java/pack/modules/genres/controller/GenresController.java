package pack.modules.genres.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.modules.genres.dto.GenresRequest;
import pack.modules.genres.dto.GenresResponse;
import pack.modules.genres.service.GenresService;

import java.util.List;

/**
 * 장르 관련 HTTP 요청을 처리하는 컨트롤러 클래스입니다.
 * - 장르 등록, 조회, 수정, 삭제에 대한 API 제공
 */
@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenresController {

    private final GenresService genresService;

    /**
     * 장르 등록 API
     * @param request 등록할 장르 정보 (name)
     * @return 생성된 장르의 ID
     */
    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody GenresRequest request) {
        Integer id = genresService.createGenre(request);
        return ResponseEntity.ok(id);
    }

    /**
     * 전체 장르 목록 조회 API
     * @return 장르 리스트 (GenresResponse DTO)
     */
    @GetMapping
    public ResponseEntity<List<GenresResponse>> getAll() {
        List<GenresResponse> list = genresService.getAllGenres();
        return ResponseEntity.ok(list);
    }

    /**
     * 단일 장르 조회 API
     * @param id 조회할 장르의 ID
     * @return 해당 ID의 장르 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<GenresResponse> getOne(@PathVariable Integer id) {
        GenresResponse result = genresService.getGenreById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 장르 수정 API
     * @param id 수정할 장르 ID
     * @param request 수정할 내용 (name)
     * @return 수정된 장르의 ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Integer> update(@PathVariable Integer id, @RequestBody GenresRequest request) {
        Integer updatedId = genresService.updateGenre(id, request);
        return ResponseEntity.ok(updatedId);
    }

    /**
     * 장르 삭제 API
     * @param id 삭제할 장르 ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        genresService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
