package pack.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pack.dto.member.*;
import pack.model.member.Genre;
import pack.model.member.Member;
import pack.repository.member.GenreRepository;
import pack.repository.member.MemberRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final GenreRepository genreRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailSender emailSender;

    private final Map<String, String> resetCodeMap = new HashMap<>();

    @Override
    public void registerMember(MemberRequest request) {
        if (memberRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
                .id(request.getId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .birthdate(request.getBirthdate())
                .gender(request.getGender())
                .role("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // 관심 장르 처리
        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            List<Genre> genres = request.getGenres().stream()
                    .map(name -> genreRepository.findByName(name)
                            .orElseThrow(() -> new IllegalArgumentException("장르 없음: " + name)))
                    .collect(Collectors.toList());
            member.setGenres(genres);
        }

        memberRepository.save(member);
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public void updateMember(MemberRequest request) {
        Member member = memberRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member.setName(request.getName());
        member.setNickname(request.getNickname());
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());
        member.setBirthdate(request.getBirthdate());
        member.setGender(request.getGender());

        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            List<Genre> genres = request.getGenres().stream()
                    .map(name -> genreRepository.findByName(name)
                            .orElseThrow(() -> new IllegalArgumentException("장르 없음: " + name)))
                    .collect(Collectors.toList());
            member.setGenres(genres);
        }

        memberRepository.save(member);
    }

    @Override
    public void changePassword(PasswordChangeRequest request) {
        Member member = memberRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        memberRepository.save(member);
    }

    @Override
    public void sendPasswordResetCode(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 계정이 없습니다."));

        String authCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        resetCodeMap.put(email, authCode);

        String subject = "[Replog] 비밀번호 재설정 인증 코드";
        String message = "인증 코드: " + authCode;

        emailSender.sendEmail(email, subject, message);
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        String storedCode = resetCodeMap.get(request.getEmail());

        if (storedCode == null || !storedCode.equals(request.getAuthCode())) {
            throw new IllegalArgumentException("인증 코드가 유효하지 않습니다.");
        }

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        memberRepository.save(member);

        resetCodeMap.remove(request.getEmail());
    }

    @Override
    public String resetPassword(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        member.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);

        return tempPassword;
    }

    @Override
    public void withdraw(WithdrawRequest request) {
        Member member = memberRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        member.setIsDeleted(true);
        member.setDeletedAt(LocalDateTime.now());

        memberRepository.save(member);
    }

    @Override
    public void updateGenres(GenreUpdateRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<Genre> genres = genreRepository.findAllById(request.getGenreIds());

        member.setGenres(genres);
        memberRepository.save(member);
    }
} 
