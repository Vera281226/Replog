package pack.service.member;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pack.dto.member.ProfileDto;
import pack.model.member.Profile;
import pack.repository.member.MemberRepository;
import pack.repository.member.ProfileRepository;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

	private final ProfileRepository profileRepository;
	private final MemberRepository memberRepository;
	
	@Override
	public ProfileDto getProfile(String memberId) {
		Profile profile = profileRepository.findByMember_MemberId(memberId);
		if(profile == null) {
			throw new IllegalArgumentException("프로필이 존재하지 않습니다.");
		}
		
		 return ProfileDto.builder()
	                .nickname(profile.getMember().getNickname())
	                .imagePath(profile.getImagePath())
	                .introduction(profile.getIntroduction())
	                .build();
	}
	
}
