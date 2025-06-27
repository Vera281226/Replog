package pack.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import pack.dto.member.SignUpRequest;
import pack.dto.member.SignUpResponse;
import pack.service.member.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest dto) {
        SignUpResponse response = authService.signUp(dto);
        return ResponseEntity.ok(response);
    }
}
