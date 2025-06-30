package pack.dto.member;

import java.util.List;

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
public class ProfileDto {
	
	private String nickname;
	private String profileImage;
	private String introduction;


    /* 관심 장르 ID 목록  */
    private List<Integer> genres;     // [{value,label}] 대신 ID 배열
}
