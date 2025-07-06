package pack.controller.member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pack.dto.member.MemberUpdateRequest;
import pack.service.member.MemberUpdateService;
import pack.util.AuthUtil;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberUpdateController {

    private final MemberUpdateService memberUpdateService;

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody MemberUpdateRequest request, HttpSession session) {
        String sessionId = AuthUtil.getCurrentMemberId();

        if (sessionId == null || !sessionId.equals(request.getMemberId())) {
            return ResponseEntity.status(401).body("로그인 정보가 일치하지 않습니다.");
        }

        memberUpdateService.updateMember(request);
        return ResponseEntity.ok("회원정보가 수정되었습니다.");
    }
}
