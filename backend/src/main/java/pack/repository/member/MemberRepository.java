package pack.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pack.model.member.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 닉네임 중복 확인
    boolean existsByNickname(String nickname);

    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);

    // 닉네임으로 회원 조회
    Optional<Member> findByNickname(String nickname);
}
