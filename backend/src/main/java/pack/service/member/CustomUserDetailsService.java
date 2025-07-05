package pack.service.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pack.model.member.Member;
import pack.repository.member.MemberRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository; // Member 테이블을 다룰 Repository

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        // memberId로 사용자 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElse(null);

        if (member == null) {
            throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
        }
        // 사용자가 없으면 예외 처리
        if (member == null) {
            throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
        }

        // 비밀번호와 권한 설정
        return User.builder()
                .username(member.getMemberId()) // member_id
                .password(member.getPwd()) // 비밀번호
                .authorities("ROLE_USER") // 권한 설정 (여기서는 기본적으로 ROLE_USER만 설정)
                .build();
    }
}