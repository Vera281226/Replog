package pack.service.member;

import pack.dto.member.SignUpRequest;
import pack.dto.member.SignUpResponse;

public interface AuthService {
    SignUpResponse signUp(SignUpRequest dto);
}
