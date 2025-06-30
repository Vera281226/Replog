package pack.modules.contentpeople.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.modules.contentpeople.dto.ContentPeopleRequest;
import pack.modules.contentpeople.dto.ContentPeopleResponse;
import pack.modules.contentpeople.model.ContentPeople;
import pack.modules.contentpeople.model.ContentPeopleId;
import pack.modules.contentpeople.repository.ContentPeopleRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 콘텐츠-인물 매핑 서비스 클래스입니다.
 * - 복합키 기반 저장, 조회, 수정, 삭제 처리
 * - People 연관 정보 포함된 응답 DTO 사용
 */
@Service
@RequiredArgsConstructor
public class ContentPeopleService {

    private final ContentPeopleRepository contentPeopleRepository;

    /**
     * 콘텐츠-인물 매핑 등록
     * 중복 매핑 방지: content_id + person_id + role 기준으로 검사
     */
    public void save(ContentPeopleRequest request) {
        ContentPeopleId id = new ContentPeopleId(
                request.getContentId(),
                request.getPersonId(),
                request.getRole()
        );

        if (contentPeopleRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 등록된 콘텐츠-인물 매핑입니다. (중복 역할 포함)"
            );
        }

        ContentPeople entity = new ContentPeople();
        entity.setContentId(request.getContentId());
        entity.setPersonId(request.getPersonId());
        entity.setRole(request.getRole());
        entity.setCharacterName(request.getCharacterName());

        contentPeopleRepository.save(entity);
    }

    /**
     * 특정 콘텐츠에 속한 전체 인물 목록 조회
     */
    public List<ContentPeopleResponse> findByContentId(int contentId) {
        return contentPeopleRepository.findAll().stream()
                .filter(e -> e.getContentId() == contentId)
                .map(ContentPeopleResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 단일 매핑 조회 (복합키 기준)
     */
    public ContentPeopleResponse findOne(int contentId, int personId, String role) {
        ContentPeopleId id = new ContentPeopleId(contentId, personId, role);
        ContentPeople entity = contentPeopleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 콘텐츠-인물 매핑이 존재하지 않습니다."
                ));
        return new ContentPeopleResponse(entity);
    }

    /**
     * 캐릭터 이름 수정
     */
    public void update(ContentPeopleRequest request) {
        ContentPeopleId id = new ContentPeopleId(
                request.getContentId(),
                request.getPersonId(),
                request.getRole()
        );

        ContentPeople entity = contentPeopleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "수정할 콘텐츠-인물 매핑이 존재하지 않습니다."
                ));

        entity.setCharacterName(request.getCharacterName());
        contentPeopleRepository.save(entity);
    }

    /**
     * 콘텐츠-인물 매핑 삭제
     */
    public void delete(int contentId, int personId, String role) {
        ContentPeopleId id = new ContentPeopleId(contentId, personId, role);

        if (!contentPeopleRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "삭제할 콘텐츠-인물 매핑이 존재하지 않습니다."
            );
        }

        contentPeopleRepository.deleteById(id);
    }
}
