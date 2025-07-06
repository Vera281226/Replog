package pack.controller.member;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import pack.dto.member.ProfileDto;
import pack.dto.member.ProfileUpdateRequest;
import pack.service.member.ProfileService;
import pack.util.AuthUtil;

@RestController
@RequestMapping("/api/member/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /* 프로필(닉네임·사진·소개) 조회 */
    @GetMapping
    public ResponseEntity<ProfileDto> getProfile(HttpSession session) {
        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(profileService.getProfile(memberId));
    }

    /* 프로필 수정: 이미지 + 텍스트 멀티파트 */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfile(
            @ModelAttribute ProfileUpdateRequest req) {

        String memberId = AuthUtil.getCurrentMemberId();
        if (memberId == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        profileService.updateProfile(memberId, req);    // ← 구현 필요
        return ResponseEntity.ok("프로필이 수정되었습니다.");
    }
}