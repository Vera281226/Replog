package pack.service.member;

import pack.dto.member.MemberInfoResponse;
import pack.dto.member.SignUpRequest;
import pack.dto.member.UserInfoResponse;

public interface MemberService {
	boolean emailExists(String email); // 이메일 중복 확인
	boolean nicknameExists(String nickname); // 닉네임 중복 확인
	boolean idExists(String id);
    void signUp(SignUpRequest request); //회원가입 처리
    boolean validateLogin(String memberId, String password); // 로그인 검증
    boolean withdraw(String memberId, String password); // 탈퇴 처리
	UserInfoResponse authenticateUser(String memberId, String password);
	UserInfoResponse getUserInfo(String memberId);
	MemberInfoResponse getMemberInfo(String memberId);
}
