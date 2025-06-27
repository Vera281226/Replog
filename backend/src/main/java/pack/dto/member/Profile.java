package pack.dto.member;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    private String id;
    private String nickname;
    private String name;
    private String introduction;
    private String profileImage;
    
    private List<String> genres;
}
