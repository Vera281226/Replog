package pack.modules.provides.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.modules.provides.dto.ProvidersRequest;
import pack.modules.provides.dto.ProvidersResponse;
import pack.modules.provides.model.Providers;
import pack.modules.provides.repository.ProvidersRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Providers 서비스 클래스
 * - 공급자 CRUD 비즈니스 로직 처리
 * - 예외 발생 시 명확하게 ResponseStatusException 을 발생시킴
 * - GlobalExceptionHandler 에서 일관된 JSON 구조로 처리됨
 */
@Service
@RequiredArgsConstructor
public class ProvidersService {

    private final ProvidersRepository providersRepository;

    /**
     * 공급자 등록
     */
    public void saveProvider(ProvidersRequest request) {
        Providers provider = new Providers();
        provider.setProviderId(request.getProviderId());
        provider.setName(request.getName());
        provider.setLogoPath(request.getLogoPath());
        providersRepository.save(provider);
    }

    /**
     * 공급자 전체 조회
     * @return ProvidersResponse 리스트
     */
    public List<ProvidersResponse> getAllProviders() {
        return providersRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 공급자 단건 조회
     *
     * - 삭제되었거나 존재하지 않는 ID 조회 시 ResponseStatusException 발생
     */
    public ProvidersResponse getProviderById(int id) {
        Providers provider = providersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 공급자를 찾을 수 없습니다."));
        return convertToResponse(provider);
    }

    /**
     * 공급자 수정
     *
     * - 없는 ID일 경우 ResponseStatusException 발생
     */
    public void updateProvider(int id, ProvidersRequest request) {
        Providers provider = providersRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 공급자를 찾을 수 없습니다."));

        provider.setName(request.getName());
        provider.setLogoPath(request.getLogoPath());
        providersRepository.save(provider);
    }

    /**
     * 공급자 삭제
     *
     * - 존재하지 않는 ID일 경우 ResponseStatusException 발생 (명확한 404 처리)
     */
    public void deleteProvider(int id) {
        if (!providersRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "해당 공급자를 찾을 수 없습니다.");
        }
        providersRepository.deleteById(id);
    }

    /**
     * Entity → DTO 변환 메서드
     * @param provider Providers 엔티티
     * @return ProvidersResponse DTO
     */
    private ProvidersResponse convertToResponse(Providers provider) {
        ProvidersResponse response = new ProvidersResponse();
        response.setProviderId(provider.getProviderId());
        response.setName(provider.getName());
        response.setLogoPath(provider.getLogoPath());
        return response;
    }
}
