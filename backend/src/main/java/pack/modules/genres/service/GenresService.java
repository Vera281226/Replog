package pack.modules.genres.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.modules.genres.dto.GenresRequest;
import pack.modules.genres.dto.GenresResponse;
import pack.modules.genres.model.Genres;
import pack.modules.genres.repository.GenresRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 장르 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * - 장르 등록, 조회, 수정, 삭제 기능 포함
 */
@Service
@RequiredArgsConstructor
public class GenresService {

    /** Genres 엔티티에 접근하는 JPA Repository */
    private final GenresRepository genresRepository;

    /**
     * 장르 등록 메서드
     */
    public Integer createGenre(GenresRequest request) {
        Genres genre = new Genres();
        genre.setGenreId(request.getGenreId());
        genre.setName(request.getName());
        genre.setCreatedAt(LocalDateTime.now());
        Genres saved = genresRepository.save(genre);
        return saved.getGenreId();
    }

    /**
     * 전체 장르 목록을 조회합니다.
     */
    public List<GenresResponse> getAllGenres() {
        return genresRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 ID에 해당하는 장르 조회
     * - 없을 경우 ResponseStatusException 발생 → 404 처리됨
     */
    public GenresResponse getGenreById(Integer id) {
        Genres genre = genresRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 장르를 찾을 수 없습니다.")); // 404 상태 + 메시지 반환
        return toResponse(genre);
    }

    /**
     * 장르 수정
     * - 없을 경우 ResponseStatusException 발생 → 404 처리됨
     */
    public Integer updateGenre(Integer id, GenresRequest request) {
        Genres genre = genresRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 장르를 찾을 수 없습니다.")); // 404 상태 + 메시지 반환
        genre.setName(request.getName());
        Genres updated = genresRepository.save(genre);
        return updated.getGenreId();
    }

    /**
     * 장르 삭제
     */
    public void deleteGenre(Integer id) {
        genresRepository.deleteById(id);
    }

    /**
     * Entity → DTO 변환
     */
    private GenresResponse toResponse(Genres genre) {
        GenresResponse response = new GenresResponse();
        response.setGenreId(genre.getGenreId()); //  필드명 통일
        response.setName(genre.getName());
        response.setCreatedAt(genre.getCreatedAt());
        return response;
    }
}
