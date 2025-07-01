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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/* ContentsService
 *
 * 콘텐츠 등록, 조회, 수정, 삭제 등 콘텐츠 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ContentsRepository contentsRepository;
    private final ContentProvidersRepository contentProvidersRepository;

    /* 콘텐츠를 등록합니다.
     * - 중복된 TMDB ID가 있을 경우 409 Conflict 예외를 발생시킵니다.
     *
     * @param request 클라이언트로부터 전달받은 콘텐츠 요청 DTO
     * @return 저장된 콘텐츠의 고유 ID
     */
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

        Contents saved = contentsRepository.save(content);
        return saved.getContentId();
    }

    /* 전체 콘텐츠 목록을 조회합니다.
     * - 플랫폼 리스트가 주어지면 해당 플랫폼에 속한 콘텐츠만 조회합니다.
     *
     * @param platforms 필터링할 플랫폼 이름 리스트 (null 또는 빈 배열이면 전체 조회)
     * @return 콘텐츠 응답 DTO 리스트
     */
    public List<ContentsResponse> getAllContents(List<String> platforms) {
        List<Contents> contentsList;

        if (platforms == null || platforms.isEmpty()) {
            contentsList = contentsRepository.findAllContentsForResponse();
        } else {
            contentsList = contentsRepository.findByPlatformNames(platforms);
        }

        return contentsList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /* 필터 조건에 따라 콘텐츠를 조회합니다.
     * - 모든 조건은 선택적이며 null 또는 빈 값이면 해당 조건은 필터링되지 않습니다.
     *
     * @param sort 정렬 기준 (popularity, oldest, latest)
     * @param releaseStart 개봉일 시작 (yyyy-MM-dd)
     * @param releaseEnd 개봉일 끝 (yyyy-MM-dd)
     * @param genres 장르 이름 리스트 (추후 확장용)
     * @param age 연령 등급 (추후 확장용)
     * @param ratingMin 최소 평점
     * @param ratingMax 최대 평점
     * @param runtimeMin 최소 러닝타임 (추후 확장용)
     * @param runtimeMax 최대 러닝타임 (추후 확장용)
     * @param platforms 플랫폼 이름 리스트
     * @return 필터링된 콘텐츠 응답 DTO 리스트
     */
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
        List<Contents> contentsList = (platforms == null || platforms.isEmpty())
                ? contentsRepository.findAllContentsForResponse()
                : contentsRepository.findByPlatformNames(platforms);

        return contentsList.stream()
                .filter(c -> {
                    if (releaseStart != null && c.getReleaseDate() != null &&
                            c.getReleaseDate().isBefore(LocalDate.parse(releaseStart))) return false;
                    if (releaseEnd != null && c.getReleaseDate() != null &&
                            c.getReleaseDate().isAfter(LocalDate.parse(releaseEnd))) return false;
                    if (ratingMin != null && c.getRating() != null && c.getRating() < ratingMin) return false;
                    if (ratingMax != null && c.getRating() != null && c.getRating() > ratingMax) return false;
                    return true;
                })
                .sorted((a, b) -> {
                    if ("oldest".equals(sort)) {
                        return a.getReleaseDate().compareTo(b.getReleaseDate());
                    } else if ("latest".equals(sort)) {
                        return b.getReleaseDate().compareTo(a.getReleaseDate());
                    }
                    return 0;
                })
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /* 콘텐츠 ID로 단일 콘텐츠를 조회합니다.
     * - 존재하지 않을 경우 404 예외를 발생시킵니다.
     *
     * @param contentId 조회할 콘텐츠의 고유 ID
     * @return 콘텐츠 응답 DTO
     */
    public ContentsResponse getContentById(Integer contentId) {
        Contents content = contentsRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 콘텐츠를 찾을 수 없습니다."));

        return toResponse(content);
    }

    /* 콘텐츠 정보를 수정합니다.
     * - 존재하지 않을 경우 404 예외를 발생시킵니다.
     *
     * @param contentId 수정할 콘텐츠 ID
     * @param request 수정할 내용이 담긴 요청 DTO
     * @return 수정된 콘텐츠의 ID
     */
    public Integer updateContent(Integer contentId, ContentsRequest request) {
        Contents content = contentsRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 콘텐츠를 찾을 수 없습니다."));

        content.setTmdbId(request.getTmdbId());
        content.setTitle(request.getTitle());
        content.setOverview(request.getOverview());
        content.setRating(request.getRating());
        content.setReleaseDate(request.getReleaseDate());
        content.setMediaType(request.getMediaType());
        content.setPosterPath(request.getPosterPath());
        content.setBackdropPath(request.getBackdropPath());

        Contents updated = contentsRepository.save(content);
        return updated.getContentId();
    }

    /* 콘텐츠 ID로 콘텐츠를 삭제합니다.
     *
     * @param contentId 삭제할 콘텐츠의 고유 ID
     */
    public void deleteContent(Integer contentId) {
        contentsRepository.deleteById(contentId);
    }

    /* Contents 엔티티를 ContentsResponse DTO로 변환합니다.
     * - 플랫폼 이름 리스트는 contentProvidersRepository 통해 조회합니다.
     *
     * @param content 변환할 Contents 엔티티
     * @return ContentsResponse DTO
     */
    private ContentsResponse toResponse(Contents content) {
        List<String> platforms = contentProvidersRepository.findProviderNamesByContentId(content.getContentId());

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
                content.getCreatedAt(),
                platforms
        );
    }
}
