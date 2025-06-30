package pack.modules.contentgenre.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.modules.contentgenre.dto.ContentGenresRequest;
import pack.modules.contentgenre.dto.ContentGenresResponse;
import pack.modules.contentgenre.model.ContentGenres;
import pack.modules.contentgenre.model.ContentGenresId;
import pack.modules.contentgenre.repository.ContentGenresRepository;
import pack.modules.genres.model.Genres;
import pack.modules.genres.repository.GenresRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 콘텐츠-장르 매핑 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ContentGenresService {

    private final ContentGenresRepository contentGenresRepository;
    private final GenresRepository genresRepository;

    /**
     * 콘텐츠-장르 매핑 등록
     * @param request 매핑할 콘텐츠 ID와 장르 ID 요청 객체
     */
    public void save(ContentGenresRequest request) {
        ContentGenres entity = new ContentGenres(request.getContentId(), request.getGenreId());
        contentGenresRepository.save(entity);
    }

    /**
     * 특정 콘텐츠에 연결된 장르 목록 조회
     * @param contentId 콘텐츠 ID
     * @return 장르 목록 응답 DTO 리스트
     * @throws ResponseStatusException 해당 콘텐츠 ID가 없거나 결과가 없을 경우 예외
     */
    public List<ContentGenresResponse> findByContentId(int contentId) {
        // 🔍 모든 매핑 중 콘텐츠 ID로 필터링
        List<ContentGenres> list = contentGenresRepository.findAll().stream()
                .filter(cg -> cg.getContentId() == contentId)
                .collect(Collectors.toList());

        // ❌ 매핑된 장르가 없으면 404 에러
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 콘텐츠에 대한 장르 매핑이 없습니다: " + contentId);
        }

        // ✅ DTO로 변환
        return list.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 콘텐츠-장르 매핑 삭제
     * @param contentId 콘텐츠 ID
     * @param genreId 장르 ID
     * @throws ResponseStatusException 해당 매핑이 존재하지 않을 경우 예외
     */
    public void delete(int contentId, int genreId) {
        ContentGenresId id = new ContentGenresId(contentId, genreId);

        // ❌ 해당 매핑이 존재하지 않으면 예외 발생
        if (!contentGenresRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 콘텐츠-장르 매핑이 존재하지 않습니다.");
        }

        contentGenresRepository.deleteById(id);
    }

    /**
     * Entity → DTO 변환 메서드
     * @param entity ContentGenres 엔티티 객체
     * @return ContentGenresResponse DTO
     */
    private ContentGenresResponse convertToResponse(ContentGenres entity) {
        ContentGenresResponse dto = new ContentGenresResponse();
        dto.setContentId(entity.getContentId());
        dto.setGenreId(entity.getGenreId());

        // ❗ 장르 ID로 장르 이름 조회. 없으면 예외 발생
        Genres genre = genresRepository.findById(entity.getGenreId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "장르 ID에 해당하는 정보가 없습니다: " + entity.getGenreId()
                ));
        dto.setGenreName(genre.getName());

        return dto;
    }
}
