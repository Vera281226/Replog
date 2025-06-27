package pack.dto.member;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest {

    private String id;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthdate;
    private String gender;
}
