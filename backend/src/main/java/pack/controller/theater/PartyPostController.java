// src/main/java/pack/controller/theater/PartyPostController.java
package pack.controller.theater;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import pack.dto.theater.PartyResponse;
import pack.dto.theater.PartyPostRequest;
import pack.service.theater.PartyPostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/partyposts")
@RequiredArgsConstructor
@Slf4j
public class PartyPostController {

    private final PartyPostService partyPostService;
    
    // 모집글 상세 조회 + 조회수 증가
    @GetMapping("/{partyPostNo}")
    public ResponseEntity<PartyResponse> getPartyPostByNo(
            @PathVariable(value = "partyPostNo") Integer partyPostNo) {
        return ResponseEntity.ok(partyPostService.getPartyPostByNo(partyPostNo));
    }
    
    // 날짜 필터 + 영화명 검색 + 해당 영화관(다중선택가능) 모집글 조회
    @GetMapping("/theaters")
    public ResponseEntity<List<PartyResponse>> getFilteredPostsByTheaters(
            @RequestParam(value = "ids", required = false) List<Integer> ids,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end,
            @RequestParam(value = "movie", required = false) String movie
    ) {
        return ResponseEntity.ok(partyPostService.getFilteredPartyPosts(ids, start, end, movie));
    }
    
    // ✅ 모집글 작성 (세션 처리 추가)
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            @RequestBody PartyPostRequest dto,
            HttpServletRequest request) {
        
        try {
            // ✅ 세션에서 사용자 정보 추출
            String memberId = extractMemberIdFromSession(request);
            String nickname = extractNicknameFromSession(request);
            
            if (memberId == null) {
                log.warn("모집글 작성 요청에서 사용자 인증 실패");
                return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "로그인이 필요합니다."));
            }
            
            log.info("모집글 작성 요청: 작성자={}, 제목={}", memberId, dto.getTitle());
            
            // ✅ 세션 정보로 DTO 보완
            dto.setMemberId(memberId);
            dto.setNickname(nickname != null ? nickname : memberId);
            
            // 모집글 생성 (채팅방 자동 생성 포함)
            PartyResponse savedPost = partyPostService.createPartyPost(dto);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "모집글이 성공적으로 작성되었습니다.",
                "data", savedPost
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("모집글 작성 요청 검증 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("모집글 작성 실패", e);
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "모집글 작성에 실패했습니다."));
        }
    }
    
    // 영화관 별 모집글 개수
    @GetMapping("/theaters/count")
    public ResponseEntity<Map<Integer, Long>> getPostCountsByTheater() {
        return ResponseEntity.ok(partyPostService.countPartyPostsByTheater());
    }
    
    // 수정
    @PutMapping("/{partyPostNo}")
    public ResponseEntity<PartyResponse> update(
            @PathVariable(value = "partyPostNo") Integer partyPostNo, 
            @RequestBody PartyPostRequest dto) {
        return ResponseEntity.ok(partyPostService.updatePartyPost(partyPostNo, dto));
    }
    
    // 삭제
    @DeleteMapping("/{partyPostNo}")
    public ResponseEntity<Void> delete(@PathVariable(value = "partyPostNo") Integer partyPostNo) {
        partyPostService.deletePartyPost(partyPostNo);
        return ResponseEntity.ok().build();
    }
    
    // ✅ 세션에서 사용자 ID 추출
    private String extractMemberIdFromSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object loginMember = session.getAttribute("loginMember");
                if (loginMember instanceof String) {
                    String memberId = ((String) loginMember).trim();
                    if (!memberId.isEmpty()) {
                        log.debug("세션에서 추출한 memberId: {}", memberId);
                        return memberId;
                    }
                }
            }
            log.warn("세션에서 loginMember를 찾을 수 없음");
            return null;
        } catch (Exception e) {
            log.error("세션에서 memberId 추출 실패", e);
            return null;
        }
    }
    
    // ✅ 세션에서 닉네임 추출 (추가 정보가 있다면)
    private String extractNicknameFromSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                // memberInfo 객체가 있다면 거기서 닉네임 추출
                Object memberInfo = session.getAttribute("memberInfo");
                if (memberInfo != null) {
                    // Member 객체라면 getNickname() 호출 (실제 구조에 맞게 수정)
                    // return ((Member) memberInfo).getNickname();
                }
                
                // 또는 별도의 nickname 세션 속성이 있다면
                Object nickname = session.getAttribute("nickname");
                if (nickname instanceof String) {
                    return (String) nickname;
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("세션에서 nickname 추출 실패", e);
            return null;
        }
    }
}
