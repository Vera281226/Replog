package pack.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pack.dto.member.MemberUpdateRequest;
import pack.model.member.Member;
import pack.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberUpdateServiceImpl implements MemberUpdateService {

    private final MemberRepository memberRepository;

    @Override
    public void updateMember(MemberUpdateRequest request) {
        // 1. 기존 회원 정보 조회
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 2. 정보 수정 (ID, password는 제외)
        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());
        member.setBirthdate(request.getBirthdate());
        member.setGender(request.getGender());

        // 3. 저장
        memberRepository.save(member);
    }
}
