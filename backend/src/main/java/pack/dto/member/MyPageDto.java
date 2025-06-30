package pack.dto.member;

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
public class MyPageDto {

    private String nickname;
    private String introduction;
    private String profileImage;

    private int reviewCount;
    private int commentCount;

//    private List<String> recentReviews;    
//    private List<String> recentRecruits;    
//    private List<String> recentApplies;     
//    private List<String> recentReports;    
}
