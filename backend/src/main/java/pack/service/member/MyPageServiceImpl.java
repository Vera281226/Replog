package pack.service.member;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pack.dto.member.MyPageDto;
import pack.model.member.Member;
import pack.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService{

	private final MemberRepository memberRepository;
	
	@Override
	public MyPageDto getMyPageInfo(String memberId) {
		Optional<Member> optionalMember = memberRepository.findById(memberId);
		
		if(optionalMember.isEmpty()) {
			throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
		}
		
		Member member = optionalMember.get();
		
		// MyPageDto 생성
		return MyPageDto.builder()
				.nickname(member.getNickname())
//				.profileImage(profile.getImageUrl())
//				.intro(profile.getIntro())
//				.postCount(postRepository.countByMemberId(memberId))
//				.commentCount(commentRepository.countByMemberId(memberId))
				.build();
	}
}
