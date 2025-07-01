package pack.dto.theater;

import java.time.LocalDateTime;

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
public class PartyPostRequest {
    private String memberId;
    private String nickname;
    private String movie;
    private String title;
    private String content;
    private LocalDateTime partyDeadline;
    private Integer theaterId;
    private Integer partyLimit;
    private String gender;
    private Integer ageGroupsMask;
}