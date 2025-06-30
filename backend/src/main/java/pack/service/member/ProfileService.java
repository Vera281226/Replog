package pack.service.member;

import pack.dto.member.ProfileDto;
import pack.dto.member.ProfileUpdateRequest;

public interface ProfileService {
	ProfileDto getProfile(String memberId);
//	void updateProfile(String memberId, ProfileDto dto);

	void updateProfile(String memberId, ProfileUpdateRequest req);
}
