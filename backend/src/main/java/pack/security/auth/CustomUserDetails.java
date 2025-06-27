package pack.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pack.model.member.Member;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(member.getRole()) // 예: "ROLE_USER"
        );
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getId(); // 로그인 ID 필드
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 X
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠김 X
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 X
    }

    @Override
    public boolean isEnabled() {
        return !member.getIsDeleted(); // 탈퇴한 회원은 비활성화
    }

    public Member getMember() {
        return member;
    }
}
