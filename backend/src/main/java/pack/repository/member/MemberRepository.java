package pack.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.member.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
    int countByEmail(String email);
    int countByNickname(String nickname);
    Optional<Member> findByEmail(String email);
}
