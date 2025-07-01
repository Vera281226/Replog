package pack.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.member.Profile;

public interface ProfileRepository extends JpaRepository<Profile, String> {
	Profile findByMember_MemberId(String memberId);  // memberId로 프로필 조회
}