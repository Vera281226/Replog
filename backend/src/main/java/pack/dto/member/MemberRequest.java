package pack.dto.member;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MemberRequest {

    private String id;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthdate;
    private String gender;

    private List<String> genres; // ← 반드시 model 패키지의 Genre로 설정
}
