package pack.dto.member;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class SignUpRequest{
	@JsonProperty("memberId")
	public String memberId;
	@JsonProperty("password")
	public String pwd;
	public String name;
	public String nickname;
	public String email;
	public String phone;
	public String address;
	public LocalDate birthdate;
	public String gender;
	
	private List<Integer> genres;
}