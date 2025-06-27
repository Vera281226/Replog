package pack.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.dto.member.MyPageProfileResponse;
import pack.dto.member.MyPageActivityResponse;
import pack.service.member.MyPageService;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/profile")
    public ResponseEntity<MyPageProfileResponse> getProfile(@RequestParam String memberId) {
        return ResponseEntity.ok(myPageService.getProfile(memberId));
    }

    @GetMapping("/activity")
    public ResponseEntity<MyPageActivityResponse> getActivity(@RequestParam String memberId) {
        return ResponseEntity.ok(myPageService.getActivity(memberId));
    }
    
}
