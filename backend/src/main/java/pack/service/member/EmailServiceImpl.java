package pack.service.member;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pack.dto.member.EmailAuthRequest;
import pack.dto.member.EmailVerifyRequest;
import pack.dto.member.PasswordResetRequest;
import pack.model.member.EmailCode;
import pack.model.member.Member;
import pack.repository.member.EmailCodeRepository;
import pack.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

	private final EmailCodeRepository emailCodeRepository;
	private final MemberRepository memberRepository;
	private final EmailSender emailSender;
	private final PasswordEncoder passwordEncoder;
	
	// 1. 인증코드 발송
	@Override
	public void sendAuthCode(EmailAuthRequest dto) {
		String code = String.format("%06d", new Random().nextInt(1000000));  // 6자리 인증코드 생성(랜덤 숫자 문자열)
		
		// 기존 인증 정보가 있으면 수정, 없으면 새로 생성
		EmailCode auth = emailCodeRepository.findByEmail(dto.getEmail())
			.map(existing -> {
				existing.setAuthCode(code);
				existing.setIsVerified(false);
				existing.setCreatedAt(LocalDateTime.now());
				existing.setExpiredAt(LocalDateTime.now().plusMinutes(3));
				return existing;
			}).orElse(
				EmailCode.builder()
					.email(dto.getEmail())
					.authCode(code)
					.isVerified(false)
					.createdAt(LocalDateTime.now())
					.expiredAt(LocalDateTime.now().plusMinutes(3))
					.build()
			);
		
		emailCodeRepository.save(auth);
		emailSender.send(dto.getEmail(), "[Replog] 인증 코드", "인증 코드는 " + code + " 입니다.");
	}
	
	// 2. 인증코드 확인
	@Override
	// 이메일과 인증코드를 확인하고, 인증 성공 시 true, 실패 시 false 
	public boolean verifyAuthCode(EmailVerifyRequest dto) {
		return emailCodeRepository.findByEmailAndAuthCode(dto.getEmail(), dto.getAuthCode())
			.filter(code -> code.getExpiredAt().isAfter(LocalDateTime.now()))  // 만료 전인지 확인
			.map(code -> {
				code.setIsVerified(true);
				emailCodeRepository.save(code); // 인증 완료 처리
				return true;
			}).orElse(false);  // 실패 시 false 반환
	}
	
	// 3. 비밀번호 재설정
	@Override
	public boolean resetPassword(PasswordResetRequest dto) {
		EmailCode code = emailCodeRepository.findByEmail(dto.getEmail())
			.filter(EmailCode::getIsVerified)  // 인증 완료된 메일인지 확인
			.orElse(null);
		
		if(code == null) return false;
		
		Member member = memberRepository.findByEmail(dto.getEmail()).orElse(null);
		if(member == null) return false;
		
		// 비밀번호 인코딩 추가
	    String encodedPassword = passwordEncoder.encode(dto.getPwd());
		member.setPwd(encodedPassword);
		memberRepository.save(member);
		
		return true;
	}
}
