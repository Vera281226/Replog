package pack.modules.people.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.modules.people.dto.PeopleRequest;
import pack.modules.people.dto.PeopleResponse;
import pack.modules.people.model.People;
import pack.modules.people.repository.PeopleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PeopleService
 * <p>
 * 인물(People) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * - 전체 조회, 단일 조회, 등록, 수정, 삭제 기능을 제공합니다.
 * - TMDB ID 중복 저장 방지를 포함합니다.
 */
@Service
@RequiredArgsConstructor
public class PeopleService {

    private final PeopleRepository peopleRepository;

    /**
     * 전체 인물 목록을 조회합니다.
     *
     * @return 모든 인물 정보를 담은 DTO 리스트
     */
    public List<PeopleResponse> getAllPeople() {
        return peopleRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 인물 정보를 ID 기준으로 조회합니다.
     *
     * @param id 내부 DB용 인물 ID (PK)
     * @return 조회된 인물 DTO
     * @throws ResponseStatusException 존재하지 않을 경우 404 반환
     */
    public PeopleResponse getPeopleById(int id) {
        People people = peopleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 인물을 찾을 수 없습니다: " + id));
        return convertToResponse(people);
    }

    /**
     * 새로운 인물 정보를 등록합니다. (TMDB ID 기준 중복 방지)
     *
     * @param request 등록할 인물 정보
     */
    public void createPeople(PeopleRequest request) {
        if (peopleRepository.findByTmdbId(request.getTmdbId()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 등록된 인물입니다. (tmdbId=" + request.getTmdbId() + ")");
        }

        People people = new People();
        people.setTmdbId(request.getTmdbId());
        people.setName(request.getName());
        people.setProfilePath(request.getProfilePath());
        people.setKnownForDepartment(request.getKnownForDepartment());

        peopleRepository.save(people);
    }

    /**
     * 기존 인물 정보를 수정합니다.
     *
     * @param id      수정할 인물의 내부 ID
     * @param request 수정 요청 DTO
     * @throws ResponseStatusException 존재하지 않을 경우 404 반환
     */
    public void updatePeople(int id, PeopleRequest request) {
        People people = peopleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 인물을 찾을 수 없습니다: " + id));

        // TMDB ID는 수정 불가 (기본 정책)
        people.setName(request.getName());
        people.setProfilePath(request.getProfilePath());
        people.setKnownForDepartment(request.getKnownForDepartment());

        peopleRepository.save(people);
    }

    /**
     * 특정 인물 정보를 삭제합니다.
     *
     * @param id 삭제할 인물의 ID
     * @throws ResponseStatusException 존재하지 않을 경우 404 반환
     */
    public void deletePeople(int id) {
        if (!peopleRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "해당 인물을 찾을 수 없습니다: " + id);
        }
        peopleRepository.deleteById(id);
    }

    /**
     * Entity → DTO 변환 메서드
     *
     * @param people Entity 객체
     * @return DTO 객체
     */
    private PeopleResponse convertToResponse(People people) {
        PeopleResponse res = new PeopleResponse();
        res.setPersonId(people.getPersonId()); // ✅ 수정 완료
        res.setTmdbId(people.getTmdbId());
        res.setName(people.getName());
        res.setProfilePath(people.getProfilePath());
        res.setKnownForDepartment(people.getKnownForDepartment());
        return res;
    }
}
