package pack.dto.member;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoResponse {
    private String memberId;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthdate;
    private String gender;
}
