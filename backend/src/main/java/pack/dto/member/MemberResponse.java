package pack.dto.member;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private String id;
    private String password;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthdate;
    private String gender;
    private String role;
    private LocalDateTime createdAt;
}
