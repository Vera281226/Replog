package pack.modules.contentprovider.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.modules.contentprovider.dto.ContentProvidersRequest;
import pack.modules.contentprovider.dto.ContentProvidersResponse;
import pack.modules.contentprovider.model.ContentProviders;
import pack.modules.contentprovider.model.ContentProvidersId;
import pack.modules.contentprovider.repository.ContentProvidersRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 콘텐츠-플랫폼 매핑 서비스 클래스
 * <p>
 * - CRUD 비즈니스 로직 처리
 * - 예외 발생 시 ResponseStatusException 반환
 */
@Service
@RequiredArgsConstructor
public class ContentProvidersService {

    private final ContentProvidersRepository repository;

    /**
     * 콘텐츠-플랫폼 매핑 저장
     *
     * @param request 요청 DTO
     */
    public void save(ContentProvidersRequest request) {
        ContentProviders entity = new ContentProviders();
        entity.setContentId(request.getContentId());
        entity.setProviderId(request.getProviderId());
        repository.save(entity);
    }

    /**
     * 전체 콘텐츠-플랫폼 매핑 목록 조회
     *
     * @return 응답 DTO 리스트
     */
    public List<ContentProvidersResponse> findAll() {
        return repository.findAll().stream().map(entity -> {
            ContentProvidersResponse dto = new ContentProvidersResponse();
            dto.setContentId(entity.getContentId());
            dto.setProviderId(entity.getProviderId());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 단일 콘텐츠-플랫폼 매핑 조회
     *
     * @param contentId  콘텐츠 ID
     * @param providerId 플랫폼 ID
     * @return 매핑 응답 DTO
     */
    public ContentProvidersResponse findById(int contentId, int providerId) {
        ContentProviders entity = repository.findById(new ContentProvidersId(contentId, providerId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 매핑을 찾을 수 없습니다."));

        ContentProvidersResponse dto = new ContentProvidersResponse();
        dto.setContentId(entity.getContentId());
        dto.setProviderId(entity.getProviderId());
        return dto;
    }

    /**
     * 콘텐츠-플랫폼 매핑 삭제
     *
     * @param contentId  콘텐츠 ID
     * @param providerId 플랫폼 ID
     */
    public void delete(int contentId, int providerId) {
        boolean exists = repository.existsById(new ContentProvidersId(contentId, providerId));
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 매핑이 존재하지 않습니다.");
        }
        repository.deleteById(new ContentProvidersId(contentId, providerId));
    }
}
