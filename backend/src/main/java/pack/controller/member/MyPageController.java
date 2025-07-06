package pack.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import pack.dto.member.MyPageDto;
import pack.service.member.MyPageService;
import pack.util.AuthUtil;

@RestController
@RequestMapping("/api/member/mypage")
@RequiredArgsConstructor
public class MyPageController {

	private final MyPageService myPageService;
	
	@GetMapping
    public ResponseEntity<MyPageDto> getMyPageInfo(HttpSession session) {
        // 세션에서 로그인한 사용자 ID 가져오기
        String memberId = AuthUtil.getCurrentMemberId();

        // 로그인 안 되어 있으면 401 Unauthorized
        if (memberId == null) {
            return ResponseEntity.status(401).build();
        }

        // 마이페이지 정보 조회
        MyPageDto dto = myPageService.getMyPageInfo(memberId);

        return ResponseEntity.ok(dto);
    }

	
//    // 내가 쓴 리뷰 펼치기
//    @GetMapping("/reviews")
//    public ResponseEntity<MyPageDto> getMyReviews(HttpSession session) {
//        String memberId = (String) session.getAttribute("loginMember");
//        MyPageDto dto = myPageService.getMyReviews(memberId);
//        return ResponseEntity.ok(dto);
//    }
//
//    // 내가 쓴 모집글 펼치기
//    @GetMapping("/recruits")
//    public ResponseEntity<MyPageDto> getMyRecruits(HttpSession session) {
//        String memberId = (String) session.getAttribute("loginMember");
//        MyPageDto dto = myPageService.getMyRecruits(memberId);
//        return ResponseEntity.ok(dto);
//    }
//
//    // 내가 신청한 내역 펼치기
//    @GetMapping("/applies")
//    public ResponseEntity<MyPageDto> getMyApplies(HttpSession session) {
//        String memberId = (String) session.getAttribute("loginMember");
//        MyPageDto dto = myPageService.getMyApplies(memberId);
//        return ResponseEntity.ok(dto);
//    }
//
//    // 내가 신고한 글 펼치기
//    @GetMapping("/reports")
//    public ResponseEntity<MyPageDto> getMyReports(HttpSession session) {
//        String memberId = (String) session.getAttribute("loginMember");
//        MyPageDto dto = myPageService.getMyReports(memberId);
//        return ResponseEntity.ok(dto);
//    }
}