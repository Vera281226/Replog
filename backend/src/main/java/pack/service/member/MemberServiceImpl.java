package pack.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pack.ProjectBackendApplication;
import pack.dto.member.SignUpRequest;
import pack.dto.member.UserInfoResponse;
import pack.model.member.Member;
import pack.repository.member.MemberRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(SignUpRequest request) {
        if (memberRepository.countByNickname(request.getNickname()) > 0) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        if (memberRepository.countByEmail(request.getEmail()) > 0) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    	//.pwd(passwordEncoder.encode(request.getPwd())) 비밀번호 encode

		Member member = Member.builder()
				.memberId(request.getMemberId())
				.pwd(passwordEncoder.encode(request.getPwd()))
				.name(request.getName())
				.nickname(request.getNickname())
				.email(request.getEmail())
				.phone(request.getPhone())
				.address(request.getAddress())
				.birthdate(request.getBirthdate())
				.gender(request.getGender())
				.role("ROLE_USER")
				.createdAt(LocalDateTime.now())
				.isDeleted(false)
				.build();

        memberRepository.save(member);
    }

	@Override
	public boolean emailExists(String email) {
		// TODO Auto-generated method stub
		return memberRepository.countByEmail(email) > 0; // 0 이면 false, 1 이상이면 true
	}

	@Override
	public boolean nicknameExists(String nickname) {
		return memberRepository.countByNickname(nickname) >0;
		
	}
	
	@Override
    public boolean validateLogin(String memberId, String password) {
        Optional<Member> optional = memberRepository.findById(memberId);
        if (optional.isPresent()) {
            Member member = optional.get();
			return passwordEncoder.matches(password, member.getPwd());
        }
        return false;
    }
	
	@Override
	public boolean withdraw(String memberId, String password) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));


		if (!passwordEncoder.matches(password, member.getPwd())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		
		member.setIsDeleted(true);
		member.setDeletedAt(LocalDateTime.now());
		memberRepository.save(member);
		
		return true;
	}
	
	@Override
	public UserInfoResponse authenticateUser(String memberId, String password) {
	    boolean isValid = validateLogin(memberId, password);
	    if (!isValid) {
	        throw new RuntimeException("인증 실패");
	    }
	    
	    Member member = memberRepository.findById(memberId)
	        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
	    
	    return UserInfoResponse.builder()
	        .memberId(member.getMemberId())
	        .nickname(member.getNickname())
	        .email(member.getEmail())
	        .build();
	}

	@Override
	public UserInfoResponse getUserInfo(String memberId) {
	    Member member = memberRepository.findById(memberId)
	        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
	    
	    return UserInfoResponse.builder()
	        .memberId(member.getMemberId())
	        .nickname(member.getNickname())
	        .email(member.getEmail())
	        .build();
	}
}
	
//	@Override
//    public void updatePassword(String email, String newPassword) {
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));
//
//        String encrypted = passwordEncoder.encode(newPassword);
//        member.setPwd(encrypted);
//        memberRepository.save(member);
//    }

