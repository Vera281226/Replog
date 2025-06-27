package pack.controller.theater;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.dto.theater.TheaterResponse;
import pack.service.theater.TheaterService;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    /**
     * 전체 영화관 조회 또는 특정 ID 목록에 해당하는 영화관 조회
     * /api/theaters            → 전체
     * /api/theaters?ids=1,2,3  → 일부 필터
     */
    @GetMapping
    public ResponseEntity<List<TheaterResponse>> getTheaters(@RequestParam(name = "ids", required = false) List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.ok(theaterService.getAllTheaters());
        } else {
            return ResponseEntity.ok(theaterService.getTheatersByIds(ids));
        }
    }
}