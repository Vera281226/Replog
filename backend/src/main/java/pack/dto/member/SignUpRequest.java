package pack.dto.member;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pack.model.member.Genre;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequest{
	public String memberId;
	public String pwd;
	public String name;
	public String nickname;
	public String email;
	public String phone;
	public String address;
	public LocalDate birthdate;
	public String gender;
	
	private List<Genre> genres;
}
