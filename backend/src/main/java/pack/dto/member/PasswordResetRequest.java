package pack.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
	
    private String email;
    
    @JsonProperty("password")
    private String pwd;	
}