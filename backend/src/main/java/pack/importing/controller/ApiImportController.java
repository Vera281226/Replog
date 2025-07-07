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
 * ✅ TMDB API로부터 데이터를 가져오는 Import 컨트롤러 클래스입니다.
 * - 장르 / 플랫폼 / 콘텐츠 / 인물 전체 정보를 가져와 DB에 저장합니다.
 * - 예능(tv)은 제외하고 영화/OTT 콘텐츠만 수집합니다.
 * -------------------------------------------------------------
 */
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ApiImportController {

    // ✅ TMDB 연동 서비스
    private final ApiImportService apiImportService;

    /**
     * ✅ 장르 목록 수집 (genres 테이블)
     */
    @PostMapping("/genres")
    public ResponseEntity<Void> importGenres() {
        apiImportService.importGenresFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * ✅ 플랫폼 목록 수집 (providers 테이블)
     */
    @PostMapping("/providers")
    public ResponseEntity<Void> importProviders() {
        apiImportService.importProvidersFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * ✅ 영화 및 OTT 콘텐츠 수집 (예능 제외)
     * 대상: contents, content_genres, content_providers
     */
    @PostMapping("/contents")
    public ResponseEntity<String> importContents() {
        try {
            apiImportService.importContentsFromTmdb(LocalDate.now().minusMonths(6));
            return ResponseEntity.ok("컨텐츠 수집 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("컨텐츠 수집 실패: " + e.getMessage());
        }
    }

    /**
     * ✅ 출연진/제작진 정보 수집
     * 대상: people, content_people
     */
    @PostMapping("/people")
    public ResponseEntity<Void> importPeopleAndCredits() {
        apiImportService.importPeopleAndCredits();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * ✅ 전체 수집 API (예능 제외, 영화/OTT 전용)
     * 포함: 장르, 플랫폼, 콘텐츠(movie), 인물/매핑
     */
    @PostMapping("/all")
    public ResponseEntity<String> importAllFromTmdb() {
        try {
            LocalDate startDate = LocalDate.now().minusMonths(6);
            
            // 1단계: 컨텐츠 선행 적재
            apiImportService.importContentsFromTmdb(startDate);
            
            // 2단계: 메타데이터 적재 (병렬 가능)
            apiImportService.importGenresFromTmdb();
            apiImportService.importProvidersFromTmdb();
//            apiImportService.importPeopleFromTmdb();
            
            // 3단계: 매핑 테이블 적재 (컨텐츠 및 메타데이터 존재 후)
//            apiImportService.importContentMappings();
            
            return ResponseEntity.ok("전체 데이터 수집 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("데이터 수집 실패: " + e.getMessage());
        }
    }

    // 🔒 예능(tv) 콘텐츠 수집은 더 이상 사용하지 않음 (기능 보존용 주석)
    /*
    @PostMapping("/variety-shows")
    public ResponseEntity<Void> importKoreanVarietyShows() {
        apiImportService.importKoreanVarietyShowsFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    */
}
