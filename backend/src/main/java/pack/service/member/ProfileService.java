package pack.service.member;

import org.springframework.web.multipart.MultipartFile;
import pack.dto.member.Profile;

public interface ProfileService {

    Profile getProfile(String memberId);

    void updateProfile(String memberId, Profile dto);

    String uploadProfileImage(String memberId, MultipartFile file);
}
