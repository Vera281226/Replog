package pack.service.member;

import pack.dto.member.ProfileDto;

public interface ProfileService {
	ProfileDto getProfile(String memberId);
//	void updateProfile(String memberId, ProfileDto dto);
}
