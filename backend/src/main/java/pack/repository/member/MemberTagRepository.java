package pack.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.member.MemberTag;
import pack.model.member.MemberTagId;

import java.util.List;

public interface MemberTagRepository extends JpaRepository<MemberTag, MemberTagId> {
    List<MemberTag> findById_MemberId(String memberId);
    void deleteById(MemberTagId id);
    void deleteAlById_MemberId(String memberId);
}
