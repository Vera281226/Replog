package pack.service.member;

import pack.dto.member.MyPageDto;

public interface MyPageService {

    MyPageDto getMyPageInfo(String memberId);
}
