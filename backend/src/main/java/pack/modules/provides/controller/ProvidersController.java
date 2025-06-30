package pack.modules.provides.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pack.modules.provides.dto.ProvidersRequest;
import pack.modules.provides.dto.ProvidersResponse;
import pack.modules.provides.service.ProvidersService;

import java.util.List;

/**
 * Providers REST 컨트롤러
 * - 공급자 등록, 조회, 수정, 삭제 API
 */
@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProvidersController {

    private final ProvidersService providersService;

    /**
     * 공급자 등록
     * POST /api/providers
     */
    @PostMapping
    public void createProvider(@RequestBody ProvidersRequest request) {
        providersService.saveProvider(request);
    }

    /**
     * 공급자 전체 조회
     * GET /api/providers
     */
    @GetMapping
    public List<ProvidersResponse> getAllProviders() {
        return providersService.getAllProviders();
    }

    /**
     * 공급자 단건 조회
     * GET /api/providers/{id}
     */
    @GetMapping("/{id}")
    public ProvidersResponse getProvider(@PathVariable int id) {
        return providersService.getProviderById(id);
    }

    /**
     * 공급자 수정
     * PUT /api/providers/{id}
     */
    @PutMapping("/{id}")
    public void updateProvider(@PathVariable int id, @RequestBody ProvidersRequest request) {
        providersService.updateProvider(id, request);
    }

    /**
     * 공급자 삭제
     * DELETE /api/providers/{id}
     */
    @DeleteMapping("/{id}")
    public void deleteProvider(@PathVariable int id) {
        providersService.deleteProvider(id);
    }
}
