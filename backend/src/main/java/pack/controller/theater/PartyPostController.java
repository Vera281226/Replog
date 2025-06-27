package pack.controller.theater;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pack.dto.theater.PartyResponse;
import pack.dto.theater.PartyPostRequest;
import pack.service.theater.PartyPostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/partyposts")
@RequiredArgsConstructor
public class PartyPostController {

    private final PartyPostService partyPostService;
    
    // 모집글 상세 조회 + 조회수 증가
    @GetMapping("/{partyPostNo}")
    public ResponseEntity<PartyResponse> getPartyPostByNo(@PathVariable("partyPostNo") Integer partyPostNo) {
        return ResponseEntity.ok(partyPostService.getPartyPostByNo(partyPostNo));
    }
    
    // 날짜 필터 + 해당 영화관(다중선택가능) 모집글 조회
    @GetMapping("/theaters")
    public ResponseEntity<List<PartyResponse>> getFilteredPostsByTheaters(
            @RequestParam(value = "ids", required = false) List<Integer> ids,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end
    ) {
        return ResponseEntity.ok(partyPostService.getFilteredPartyPosts(ids, start, end));
    }
    
    // 모집글 작성
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody PartyPostRequest dto) {
        partyPostService.createPartyPost(dto);
        return ResponseEntity.ok().build();
    }
    // 영화관 별 모집글 개수
    @GetMapping("/theaters/count")
    public ResponseEntity<Map<Integer, Long>> getPostCountsByTheater() {
        return ResponseEntity.ok(partyPostService.countPartyPostsByTheater());
    }
    
    
    // 수정(파티원 모집했는데 시간이나 위치 등 조건 변경하면 안되니 필요 없을 거 같지만 우선 만들어둠)
    @PutMapping("/{partyPostNo}")
    public ResponseEntity<PartyResponse> update(@PathVariable("partyPostNo") Integer partyPostNo, @RequestBody PartyPostRequest dto) {
        return ResponseEntity.ok(partyPostService.updatePartyPost(partyPostNo, dto));
    }
    // 삭제(파티원 모집해서 약속이 잡혔는데 파티장 맘대로 삭제하면 안 될 거 같지만 우선 만들어둠)
    @DeleteMapping("/{partyPostNo}")
    public ResponseEntity<Void> delete(@PathVariable("partyPostNo") Integer partyPostNo) {
        partyPostService.deletePartyPost(partyPostNo);
        return ResponseEntity.ok().build();
    }
}
