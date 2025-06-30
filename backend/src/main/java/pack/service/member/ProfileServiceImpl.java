// src/main/java/pack/service/member/ProfileServiceImpl.java
package pack.service.member;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pack.common.FileStore;
import pack.dto.member.ProfileDto;
import pack.dto.member.ProfileUpdateRequest;
import pack.model.member.Genre;
import pack.model.member.Member;
import pack.model.member.Profile;
import pack.repository.member.GenreRepository;
import pack.repository.member.MemberRepository;
import pack.repository.member.ProfileRepository;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberRepository  memberRepository;
    private final GenreRepository   genreRepository;
    private final FileStore         fileStore;

    @Override
    public ProfileDto getProfile(String memberId) {

        Profile profile = profileRepository.findByMember_MemberId(memberId);
        if (profile == null)
            throw new IllegalArgumentException("프로필이 존재하지 않습니다.");

        // 회원이 보유한 장르 ID 추출
        List<Integer> genreIds = profile.getMember()
                                        .getGenres()          // List<Genre>
                                        .stream()
                                        .map(Genre::getGenreId)
                                        .toList();

        return ProfileDto.builder()
                .nickname     (profile.getMember().getNickname())
                .introduction (profile.getIntroduction())
                .profileImage (profile.getProfileImage())   // DB → DTO
                .genres       (genreIds)
                .build();
    }

    /* ---------- 프로필 수정 ---------- */
    @Transactional
    @Override
    public void updateProfile(String memberId, ProfileUpdateRequest req) {
        /* 1) 엔티티 조회 */
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        Profile profile = profileRepository.findByMember_MemberId(memberId);
        if (profile == null)
            profile = Profile.builder().member(member).build();

        /* 2) 닉네임 중복 검사 & 수정 */
        if (req.getNickname() != null &&
           !req.getNickname().equals(member.getNickname())) {

            if (memberRepository.countByNickname(req.getNickname()) > 0)
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");

            member.setNickname(req.getNickname());
        }

        /* 3) 소개글 수정 */
        if (req.getIntroduction() != null)
            profile.setIntroduction(req.getIntroduction());

        /* 4) 관심 장르 갱신 */
        if (req.getGenres() != null && !req.getGenres().isEmpty()) {
            List<Genre> newGenres = genreRepository.findAllById(req.getGenres());
            member.getGenres().clear();
            member.getGenres().addAll(newGenres);
        }

        /* 5) 이미지 파일 저장 */
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            try {
                String path = fileStore.saveProfileImage(req.getImage());
                profile.setProfileImage(path);
            } catch (Exception e) {
                throw new RuntimeException("이미지 저장 실패", e);
            }
        }

        /* 6) 저장 */
        profileRepository.save(profile);
        memberRepository.save(member);
    }
}
