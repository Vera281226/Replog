package pack.modules.contents.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.modules.contents.dto.ContentsRequest;
import pack.modules.contents.dto.ContentsResponse;
import pack.modules.contents.model.Contents;
import pack.modules.contents.repository.ContentsRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 콘텐츠 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * - 콘텐츠 등록, 조회, 수정, 삭제 등의 기능 포함
 */
@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ContentsRepository contentsRepository;

    /**
     * 콘텐츠를 등록합니다.
     * - 중복된 TMDB ID가 있으면 409 Conflict 예외 발생
     *
     * @param request 클라이언트로부터 전달받은 콘텐츠 요청 DTO
     * @return 저장된 콘텐츠의 ID
     */
    public Integer createContent(ContentsRequest request) {
        // 중복 TMDB ID 검사
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

    /**
     * 전체 콘텐츠 목록을 조회합니다.
     *
     * @return 콘텐츠 목록 DTO 리스트
     */
    public List<ContentsResponse> getAllContents() {
        return contentsRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 콘텐츠 ID로 단일 콘텐츠를 조회합니다.
     * - 존재하지 않으면 404 예외 발생
     *
     * @param contentId 콘텐츠 고유 ID
     * @return 콘텐츠 응답 DTO
     */
    public ContentsResponse getContentById(Integer contentId) {
        Contents content = contentsRepository.findById(contentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 콘텐츠를 찾을 수 없습니다."));
        return toResponse(content);
    }

    /**
     * 콘텐츠를 수정합니다.
     * - 존재하지 않으면 404 예외 발생
     *
     * @param contentId 수정할 콘텐츠 ID
     * @param request 수정 요청 DTO
     * @return 수정된 콘텐츠 ID
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

    /**
     * 콘텐츠 ID로 콘텐츠를 삭제합니다.
     *
     * @param contentId 삭제할 콘텐츠 ID
     */
    public void deleteContent(Integer contentId) {
        contentsRepository.deleteById(contentId);
    }

    /**
     * Contents 엔티티를 ContentsResponse DTO로 변환합니다.
     *
     * @param content Contents 엔티티
     * @return ContentsResponse DTO
     */
    private ContentsResponse toResponse(Contents content) {
        ContentsResponse response = new ContentsResponse();
        response.setContentId(content.getContentId());
        response.setTmdbId(content.getTmdbId());
        response.setTitle(content.getTitle());
        response.setOverview(content.getOverview());
        response.setRating(content.getRating());
        response.setReleaseDate(content.getReleaseDate());
        response.setMediaType(content.getMediaType());
        response.setPosterPath(content.getPosterPath());
        response.setBackdropPath(content.getBackdropPath());
        response.setCreatedAt(content.getCreatedAt());
        return response;
    }
}
