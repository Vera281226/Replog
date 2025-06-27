package pack.dto.member;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageActivityResponse {

    private int reviewCount;
    private int commentCount;
    private int postCount;
    private int reportCount;
    private int applyCount;
}