package pack.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import pack.dto.member.SignUpRequest;
import pack.dto.member.SignUpResponse;
import pack.model.member.Member;
import pack.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignUpResponse signUp(SignUpRequest dto) {

        if (memberRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 ID입니다.");
        }

        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (memberRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .id(dto.getId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .birthdate(dto.getBirthdate())
                .gender(dto.getGender())
                .role("ROLE_USER")
                .build();

        Member saved = memberRepository.save(member);

        return SignUpResponse.builder()
                .id(saved.getId())
                .nickname(saved.getNickname())
                .email(saved.getEmail())
                .build();
    }
}
