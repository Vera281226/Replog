package pack.service.member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private final String memberId;
    private final String nickname;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String memberId, String nickname, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.password = password;
        this.authorities = authorities;
    }

    public String getMemberId() { return memberId; }
    public String getNickname() { return nickname; }

    @Override
    public String getUsername() { return memberId; }
    @Override
    public String getPassword() { return password; }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
