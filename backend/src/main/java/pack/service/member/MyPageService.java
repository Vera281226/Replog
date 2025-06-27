package pack.service.member;

import pack.dto.member.MyPageProfileResponse;
import pack.dto.member.MyPageActivityResponse;

public interface MyPageService {

    MyPageProfileResponse getProfile(String memberId);

    MyPageActivityResponse getActivity(String memberId);
       
}
