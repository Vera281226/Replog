package pack.service.member;


import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pack.dto.member.MyPageDto;
import pack.model.member.Member;
import pack.model.member.Profile;
import pack.repository.board.CommentRepository;
import pack.repository.board.PostRepository;
import pack.repository.member.MemberRepository;
import pack.repository.member.ProfileRepository;


@Service
@RequiredArgsConstructor
@Transactional
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository   memberRepository;
    private final ProfileRepository  profileRepository;
    private final PostRepository     postRepository;
    private final CommentRepository  commentRepository;

    @Override
    public MyPageDto getMyPageInfo(String memberId) {

        /* 1) 회원 + 프로필 조회 */
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        Profile profile = profileRepository.findByMember_MemberId(memberId);

        /* 2) 게시글·댓글 수 계산 (필요 없으면 0으로 둬도 무방) */
        int postCnt    = postRepository    != null ? postRepository.countByMemberId(memberId)    : 0;
        int commentCnt = commentRepository != null ? commentRepository.countByMemberId(memberId) : 0;

        /* 3) DTO 반환 */
        return MyPageDto.builder()
                .nickname      (member.getNickname())
                .introduction  (profile != null ? profile.getIntroduction() : null)
                .profileImage  (profile != null ? profile.getProfileImage() : null)
                .reviewCount   (postCnt)
                .commentCount  (commentCnt)
                .build();
    }
}