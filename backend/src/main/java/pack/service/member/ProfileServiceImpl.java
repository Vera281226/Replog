package pack.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pack.dto.member.Profile;
import pack.model.member.Genre;
import pack.model.member.Member;
import pack.repository.member.GenreRepository;
import pack.repository.member.MemberRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final MemberRepository memberRepository;
    private final GenreRepository genreRepository;

    private final String uploadDir = "src/main/resources/static/uploads/profile/";
    private final String accessPath = "/uploads/profile/";

    // 프로필 조회
    @Override
    public Profile getProfile(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<String> genres = member.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toList());

        return Profile.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .name(member.getName())
                .genres(genres)
                .build();
    }

    // 프로필 정보 수정 (닉네임, 소개글, 관심 장르)
    @Override
    public void updateProfile(String memberId, Profile dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member.setNickname(dto.getNickname());

        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            List<Genre> genres = dto.getGenres().stream()
                    .map(name -> genreRepository.findByName(name)
                            .orElseThrow(() -> new IllegalArgumentException("장르 없음: " + name)))
                    .collect(Collectors.toList());

            member.setGenres(genres);
        }

        memberRepository.save(member);
    }

    // 프로필 이미지 업로드
    @Override
    public String uploadProfileImage(String memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedName = UUID.randomUUID() + extension;

        File targetFile = new File(uploadDir + savedName);
        targetFile.getParentFile().mkdirs();

        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
        
        memberRepository.save(member);

        return accessPath + savedName;
    }
}
