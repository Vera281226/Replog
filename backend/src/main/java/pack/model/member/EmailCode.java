package pack.model.member;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailCode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "email_code_id")
	private Integer emailCodeId;
	
	private String email;
	private String authCode;
	private Boolean isVerified;
	private LocalDateTime createdAt;
	private LocalDateTime expiredAt;
}

