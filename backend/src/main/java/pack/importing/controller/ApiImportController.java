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

    // TMDB 수집 로직을 포함한 서비스 주입
    private final ApiImportService apiImportService;

    /**
     * 전체 데이터 수집 API (Movie + TV 통합)
     * -------------------------------------------------------------
     * 포함 항목:
     *  - 장르 (genres) → movie + tv
     *  - 플랫폼 (providers) → OTT 정보
     *  - 콘텐츠 (contents) → 영화 + OTT 드라마 (예능은 저장 제외됨)
     *  - 인물/출연진 (people, content_people)
     * -------------------------------------------------------------
     * 요청 경로: POST /api/import/all
     */
    @PostMapping("/all")
    public ResponseEntity<String> importAllFromTmdb() {
        try {
            // ✅ 수집 시작일 기준: 2023년 1월 1일부터 오늘까지
            LocalDate startDate = LocalDate.of(2023, 1, 1);

            // 1단계: 메타데이터 우선 적재 (Movie + TV 장르 포함)
            apiImportService.importGenresFromTmdb();
            apiImportService.importProvidersFromTmdb();

            // 2단계: 콘텐츠 적재 (Movie + TV 포함, 예능은 무시됨)
            apiImportService.importAllContentsFromTmdb(startDate);

            // 3단계: 출연진 및 감독 등 People + 매핑 정보 적재
            apiImportService.importPeopleAndCredits();

            return ResponseEntity.ok("✅ 전체 데이터(Movie + TV + People) 수집 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ 데이터 수집 실패: " + e.getMessage());
        }
    }
}
