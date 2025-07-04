package pack.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pack.model.member.EmailCode;

public interface EmailCodeRepository extends JpaRepository<EmailCode, Integer>{

	Optional<EmailCode> findByEmail(String email); // 이메일로 인증 정보 조회 + 기존에 발급된 인증 코드가 있는지 확인 (코드 전송 전에 사용)
	
	Optional<EmailCode> findByEmailAndAuthCode(String email, String authCode); // 이메일 + 인증코드로 인증 정보 조회 (코드 검증 시 사용)
}
