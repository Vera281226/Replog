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
 * TMDB API로부터 데이터를 가져오는 Import 컨트롤러 클래스입니다.
 *
 * - 장르 / 플랫폼 / 콘텐츠 / 인물 전체 정보를 가져와 DB에 저장합니다.
 * - TMDB 연동은 importing.service.ApiImportService를 통해 처리됩니다.
 */
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ApiImportController {

    private final ApiImportService apiImportService;

    /**
     * TMDB에서 영화 장르 목록을 가져와 DB에 저장합니다.
     * 대상 테이블: genres
     */
    @PostMapping("/genres")
    public ResponseEntity<Void> importGenres() {
        apiImportService.importGenresFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * TMDB에서 OTT 플랫폼 목록을 가져와 DB에 저장합니다.
     * 대상 테이블: providers
     */
    @PostMapping("/providers")
    public ResponseEntity<Void> importProviders() {
        apiImportService.importProvidersFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * TMDB에서 인기 콘텐츠 목록을 가져와 DB에 저장합니다.
     * 대상 테이블: contents
     */
    @PostMapping("/contents")
    public ResponseEntity<Void> importContents() {
        apiImportService.importContentsFromTmdb();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * TMDB에서 출연진 및 제작진 정보를 가져와 DB에 저장합니다.
     * 대상 테이블: people, content_people
     */
    @PostMapping("/people")
    public ResponseEntity<Void> importPeopleAndCredits() {
        apiImportService.importPeopleAndCredits();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * TMDB에서 전체 정보를 통합적으로 가져와 DB에 저장합니다.
     * 포함 항목: genres, providers, contents, people, content_genres, content_providers, content_people
     * 조건: 오늘 기준 3개월 전부터의 콘텐츠만 수집
     */
    @PostMapping("/all")
    public ResponseEntity<Void> importAllFromTmdb() {
        apiImportService.importAllFromTmdbSince(LocalDate.now().minusMonths(3));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
