package pack.importing.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pack.importing.service.ApiImportService;

/**
 * ApiImportController
 *
 * ✅ TMDB API로부터 데이터를 가져오는 Import 컨트롤러 클래스입니다.
 * - TMDB 연동은 importing.service.ApiImportService 내부에서 처리됩니다.
 * - 이 컨트롤러는 관리자 또는 배치 서버에서 호출하는 REST API 엔드포인트를 제공합니다.
 *
 * ✅ 제공하는 API 목록:
 * - POST /api/import/genres            : 장르 수집
 * - POST /api/import/providers         : 플랫폼 수집
 * - POST /api/import/variety-shows     : 예능(tv) 콘텐츠 수집 (2006년~)
 * - POST /api/import/people            : 출연진/감독 수집
 * - POST /api/import/all               : 전체 수집 (현재는 예능(tv) 중심)
 *
 * ✅ HTTP 메서드 사용 기준:
 * - POST: TMDB 데이터를 가져와서 우리 DB에 "저장(insert/update)"하는 것이므로 반드시 POST 사용
 */
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ApiImportController {

    private final ApiImportService apiImportService;

    /**
     * ✅ TMDB에서 영화 장르 목록을 가져와 DB에 저장합니다.
     * 대상 테이블: genres
     */
    @PostMapping("/genres")
    public ResponseEntity<Void> importGenres() {
        apiImportService.importGenresFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * ✅ TMDB에서 OTT 플랫폼 목록을 가져와 DB에 저장합니다.
     * 대상 테이블: providers
     */
    @PostMapping("/providers")
    public ResponseEntity<Void> importProviders() {
        apiImportService.importProvidersFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * ✅ TMDB에서 한국 오리지널 예능(tv) 콘텐츠를 수집하여 저장합니다.
     * 조건: 2006년 1월 1일 이후 방영된 콘텐츠, 인기순 정렬
     * 대상 테이블: contents, content_genres, content_providers
     */
    @PostMapping("/variety-shows")
    public ResponseEntity<Void> importKoreanVarietyShows() {
        apiImportService.importKoreanVarietyShowsFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * ✅ TMDB에서 출연진 및 제작진 정보를 가져와 DB에 저장합니다.
     * 대상 테이블: people, content_people
     */
    @PostMapping("/people")
    public ResponseEntity<Void> importPeopleAndCredits() {
        apiImportService.importPeopleAndCredits();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * ✅ TMDB에서 전체 정보를 통합적으로 가져와 DB에 저장합니다.
     * 포함 항목: 장르, 플랫폼, 예능 콘텐츠(tv), 인물/매핑 정보
     * - 예능은 2006년 1월 1일부터 고정 기준으로 가져옵니다.
     * - startDate 파라미터는 현재 사용되지 않으며 향후 영화 수집 확장 시 활용 예정입니다.
     */
    @PostMapping("/all")
    public ResponseEntity<Void> importAllFromTmdb() {
        apiImportService.importAllFromTmdbSince(LocalDate.now().minusMonths(3));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ===============================================================
    // 🔒 기존 영화 기반 콘텐츠 수집 API는 현재 사용하지 않음 (아래는 주석으로 보존)
    // ===============================================================
    /*
    @PostMapping("/contents")
    public ResponseEntity<Void> importContents() {
        apiImportService.importContentsFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    */
}
