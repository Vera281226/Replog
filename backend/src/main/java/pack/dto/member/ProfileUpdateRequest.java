package pack.dto.member;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 프로필 수정 요청 DTO
 * multipart/form-data 로 전달되므로 <input name="..."> 이름과 필드명이 반드시 일치해야 한다.
 *
 *  ┌ nickname      : 새 닉네임
 *  ├ introduction  : 한 줄 소개
 *  ├ genres        : 관심 장르 ID 목록(예: genres=1&genres=3&genres=5)
 *  └ image         : 프로필 이미지 파일
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {
	
    private String nickname;

    private String introduction;

    private List<Integer> genres;

    private MultipartFile image;
}