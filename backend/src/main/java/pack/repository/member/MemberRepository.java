package pack.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.member.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
	Optional<Member> findByMemberId(String memberId);
    Optional<Member> findByEmail(String email);
    long countByNickname(String nickname);
    long countByEmail(String email);
}
