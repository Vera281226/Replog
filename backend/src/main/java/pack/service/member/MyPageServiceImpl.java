package pack.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pack.dto.member.MyPageProfileResponse;
import pack.dto.member.MyPageActivityResponse;
import pack.model.member.Member;
import pack.repository.member.MemberRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;

    @Override
    public MyPageProfileResponse getProfile(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return MyPageProfileResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .genres(member.getGenres().stream()
                        .map(genre -> genre.getName())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public MyPageActivityResponse getActivity(String memberId) {
        // 활동 수는 다른 사람의 코드와 연동해서 추후 적용
        return MyPageActivityResponse.builder()
                .commentCount(0)
                .postCount(0)
                .reportCount(0)
                .applyCount(0)
                .build();
    }
}
