package pack.modules.contents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.modules.contents.dto.ContentsRequest;
import pack.modules.contents.dto.ContentsResponse;
import pack.modules.contents.model.Contents;
import pack.modules.contents.repository.ContentsRepository;
import pack.modules.contentprovider.repository.ContentProvidersRepository;
import pack.modules.contentgenre.repository.ContentGenresRepository;
import pack.modules.genres.model.Genres;
import pack.modules.genres.repository.GenresRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ContentsService
 * ---------------------------------------------------------------------
 * ○ 콘텐츠 등록, 조회, 수정, 삭제 등의 비즈니스 로직 처리 클래스
 * ○ DB와 직접 통신하며 Repository 계층을 활용함
 * ---------------------------------------------------------------------
 */
@Service
@RequiredArgsConstructor
public class ContentsService {

    // 콘텐츠 저장소
    private final ContentsRepository contentsRepository;

    // 콘텐츠-플랫폼 매핑 저장소
    private final ContentProvidersRepository contentProvidersRepository;

    // 콘텐츠-장르 매핑 저장소
    private final ContentGenresRepository contentGenresRepository;

    // 장르 저장소 (이름 → ID 변환용)
    private final GenresRepository genresRepository;

    // ---------------------------------------------------------------------
    // 콘텐츠 등록
    // ---------------------------------------------------------------------
    public Integer createContent(ContentsRequest request) {
        if (contentsRepository.findByTmdbId(request.getTmdbId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 콘텐츠입니다.");
        }

        Contents content = new Contents();
        content.setTmdbId(request.getTmdbId());
        content.setTitle(request.getTitle());
        content.setOverview(request.getOverview());
        content.setRating(request.getRating());
        content.setReleaseDate(request.getReleaseDate());
        content.setMediaType(request.getMediaType());
        content.setPosterPath(request.getPosterPath());
        content.setBackdropPath(request.getBackdropPath());
        content.setRuntime(request.getRuntime());
        content.setAgeRating(request.getAgeRating());

        Contents saved = contentsRepository.save(content);
        return saved.getContentId();
    }

    // ---------------------------------------------------------------------
    // 전체 콘텐츠 조회 (플랫폼 필터 포함)
    // ---------------------------------------------------------------------
    public List<ContentsResponse> getAllContents(List<String> platforms) {
        List<Contents> contentsList = (platforms == null || platforms.isEmpty())
                ? contentsRepository.findAllContentsForResponse()
                : contentsRepository.findByPlatformNames(platforms);

        return contentsList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // 필터 조건 기반 콘텐츠 조회
    // - 장르+플랫폼은 DB에서 필터
    // - 나머지는 Java Stream 처리
    // ---------------------------------------------------------------------
    public List<ContentsResponse> getFilteredContents(
            String sort,
            String releaseStart,
            String releaseEnd,
            List<String> genres,
            String age,
            Float ratingMin,
            Float ratingMax,
            Integer runtimeMin,
            Integer runtimeMax,
            List<String> platforms
    ) {
        List<Integer> genreIds = (genres == null || genres.isEmpty()) ? null :
                genres.stream()
                        .map(name -> genresRepository.findByName(name)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 장르: " + name))
                                .getGenreId())
                        .collect(Collectors.toList());

        List<Contents> contentsList = contentsRepository.findByGenreIdsAndPlatforms(genreIds, platforms);

        return contentsList.stream()
                .filter(c -> {
                    if (releaseStart != null && c.getReleaseDate() != null &&
                            c.getReleaseDate().isBefore(LocalDate.parse(releaseStart))) return false;
                    if (releaseEnd != null && c.getReleaseDate() != null &&
                            c.getReleaseDate().isAfter(LocalDate.parse(releaseEnd))) return false;
                    if (ratingMin != null && c.getRating() != null && c.getRating() < ratingMin) return false;
                    if (ratingMax != null && c.getRating() != null && c.getRating() > ratingMax) return false;
                    if (runtimeMin != null && c.getRuntime() != null && c.getRuntime() < runtimeMin) return false;
                    if (runtimeMax != null && c.getRuntime() != null && c.getRuntime() > runtimeMax) return false;
                    if (age != null && !age.isBlank() && c.getAgeRating() != null && !c.getAgeRating().equals(age)) return false;
                    return true;
                })
                .sorted((a, b) -> {
                    LocalDate dateA = a.getReleaseDate();
                    LocalDate dateB = b.getReleaseDate();
                    if (dateA == null || dateB == null) return 0;
                    if ("oldest".equals(sort)) return dateA.compareTo(dateB);
                    else if ("latest".equals(sort)) return dateB.compareTo(dateA);
                    return 0;
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // 콘텐츠 단건 조회
    // ---------------------------------------------------------------------
    public ContentsResponse getContentById(Integer contentId) {
        Contents content = contentsRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 콘텐츠를 찾을 수 없습니다."));
        return toResponse(content);
    }

    // ---------------------------------------------------------------------
    // 콘텐츠 수정
    // ---------------------------------------------------------------------
    public Integer updateContent(Integer contentId, ContentsRequest request) {
        Contents content = contentsRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 콘텐츠를 찾을 수 없습니다."));

        content.setTmdbId(request.getTmdbId());
        content.setTitle(request.getTitle());
        content.setOverview(request.getOverview());
        content.setRating(request.getRating());
        content.setReleaseDate(request.getReleaseDate());
        content.setMediaType(request.getMediaType());
        content.setPosterPath(request.getPosterPath());
        content.setBackdropPath(request.getBackdropPath());
        content.setRuntime(request.getRuntime());
        content.setAgeRating(request.getAgeRating());

        Contents updated = contentsRepository.save(content);
        return updated.getContentId();
    }

    // ---------------------------------------------------------------------
    // 콘텐츠 삭제
    // ---------------------------------------------------------------------
    public void deleteContent(Integer contentId) {
        contentsRepository.deleteById(contentId);
    }

    // ---------------------------------------------------------------------
    // Entity → DTO 변환 메서드
    // ---------------------------------------------------------------------
    private ContentsResponse toResponse(Contents content) {
        List<String> platforms = contentProvidersRepository.findProviderNamesByContentId(content.getContentId());
        List<String> genres = contentGenresRepository.findGenreNamesByContentId(content.getContentId());

        return new ContentsResponse(
                content.getContentId(),
                content.getTmdbId(),
                content.getTitle(),
                content.getOverview(),
                content.getRating(),
                content.getReleaseDate(),
                content.getMediaType(),
                content.getPosterPath(),
                content.getBackdropPath(),
                content.getRuntime(),
                content.getAgeRating(),
                content.getCreatedAt(),
                platforms,
                genres
        );
    }
}
