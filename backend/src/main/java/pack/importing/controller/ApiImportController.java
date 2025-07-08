package pack.importing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.importing.service.ApiImportService;
import java.time.LocalDate;

/**
 * ApiImportController
 * -------------------------------------------------------------
 * TMDB API로부터 Movie/TV 통합 데이터를 가져오는 컨트롤러
 * /api/import/all 엔드포인트 중심의 핵심 기능만 제공
 * -------------------------------------------------------------
 */
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ApiImportController {

    private final ApiImportService apiImportService;

    /**
     * 전체 데이터 수집 API (Movie + TV 통합)
     * 포함: 장르, 플랫폼, 콘텐츠(movie/tv), 인물/매핑
     */
    @PostMapping("/all")
    public ResponseEntity<String> importAllFromTmdb() {
        try {
            LocalDate startDate = LocalDate.now().minusMonths(1);
            
            // 1단계: 메타데이터 우선 적재 (Movie + TV 장르 포함)
            apiImportService.importGenresFromTmdb();
            apiImportService.importProvidersFromTmdb();
            
            // 2단계: 콘텐츠 적재 (Movie + TV)
            apiImportService.importAllContentsFromTmdb(startDate);
            
            // 3단계: People 적재 (Movie + TV 모두 처리)
            apiImportService.importPeopleAndCredits();
            
            return ResponseEntity.ok("전체 데이터(Movie + TV + People) 수집 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("데이터 수집 실패: " + e.getMessage());
        }
    }
}
