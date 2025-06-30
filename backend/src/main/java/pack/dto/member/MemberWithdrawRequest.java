package pack.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberWithdrawRequest {
    private String pwd; // 사용자가 탈퇴 확인을 위해 입력하는 비밀번호
}
