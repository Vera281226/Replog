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
public class PartyResponse {
	private String id;
    private Integer partyPostNo;
    private String nickname;
    private String movie;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime partyDeadline;
    private Boolean isTerminated;
    private Boolean isHidden;
    private Integer views;
    private Integer theaterId;
    private Integer partyLimit;
    private String gender;
    private Integer ageGroupsMask;
    
    private String theaterName;
}