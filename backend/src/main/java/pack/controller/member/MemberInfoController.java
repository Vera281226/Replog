package pack.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pack.dto.member.MemberInfoResponse;
import pack.security.auth.CustomUserDetails;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberInfoController {

    @GetMapping("/me")
    public MemberInfoResponse getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return MemberInfoResponse.builder()
                .id(userDetails.getUsername())
                .name(userDetails.getMember().getName())
                .nickname(userDetails.getMember().getNickname())
                .email(userDetails.getMember().getEmail())
                .role(userDetails.getMember().getRole())
                .build();
    }
}
