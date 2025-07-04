package pack.service.theater;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import jakarta.transaction.Transactional;
import pack.dto.chat.ChatRoomRequest;
import pack.dto.chat.ChatRoomResponse;
import pack.dto.theater.PartyResponse;
import pack.dto.chat.ChatMessageRequest;
import pack.dto.theater.PartyPostRequest;
import pack.model.theater.PartyPost;
import pack.model.chat.ChatRoom.RoomType;
import pack.repository.member.MemberRepository;
import pack.repository.theater.PartyPostRepository;
import pack.repository.theater.TheaterRepository;
import pack.service.chat.ChatMessageService;
import pack.service.chat.ChatRoomService;

import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyPostServiceImpl implements PartyPostService {

	private final MemberRepository memberRepository;
	private final TheaterRepository theaterRepository;
    private final PartyPostRepository partyPostRepository;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Override
    public PartyResponse getPartyPostByNo(Integer partyPostNo) {
        PartyPost post = partyPostRepository.findById(partyPostNo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "모집글을 찾을 수 없습니다."
                ));

        post.setViews(post.getViews() + 1);
        PartyPost updated = partyPostRepository.save(post);

        return toDto(updated);
    }

    @Override
    public List<PartyResponse> getFilteredPartyPosts(List<Integer> theaterIds, String start, String end, String movie) {
        LocalDateTime now = LocalDateTime.now();
        boolean hasDateFilter = start != null && !start.isBlank() && end != null && !end.isBlank();
        boolean hasMovieFilter = movie != null && !movie.isBlank();

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        if (hasDateFilter) {
            startDateTime = LocalDate.parse(start).atStartOfDay();
            endDateTime = LocalDate.parse(end).atTime(23, 59, 59);
        }

        List<PartyPost> posts;

        if (theaterIds == null || theaterIds.isEmpty()) {
            if (hasDateFilter && hasMovieFilter) {
                posts = partyPostRepository.findByPartyDeadlineBetweenAndPartyDeadlineAfterAndMovieContainingIgnoreCase(
                        startDateTime, endDateTime, now, movie);
            } else if (hasDateFilter) {
                posts = partyPostRepository.findByPartyDeadlineBetweenAndPartyDeadlineAfter(
                        startDateTime, endDateTime, now);
            } else if (hasMovieFilter) {
                posts = partyPostRepository.findByPartyDeadlineAfterAndMovieContainingIgnoreCase(
                        now, movie);
            } else {
                posts = partyPostRepository.findByPartyDeadlineAfter(now);
            }
        } else {
            if (hasDateFilter && hasMovieFilter) {
                posts = partyPostRepository.findByTheaterIdInAndPartyDeadlineBetweenAndPartyDeadlineAfterAndMovieContainingIgnoreCase(
                        theaterIds, startDateTime, endDateTime, now, movie);
            } else if (hasDateFilter) {
                posts = partyPostRepository.findByTheaterIdInAndPartyDeadlineBetweenAndPartyDeadlineAfter(
                        theaterIds, startDateTime, endDateTime, now);
            } else if (hasMovieFilter) {
                posts = partyPostRepository.findByTheaterIdInAndPartyDeadlineAfterAndMovieContainingIgnoreCase(
                        theaterIds, now, movie);
            } else {
                posts = partyPostRepository.findByTheaterIdInAndPartyDeadlineAfter(theaterIds, now);
            }
        }

        return posts.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PartyResponse createPartyPost(PartyPostRequest dto) {
        validatePartyPostRequest(dto);
        validateMemberExists(dto.getMemberId());

        PartyPost post = toEntity(dto);
        PartyPost savedPost = partyPostRepository.save(post);

        ChatRoomRequest chatRoomRequest = ChatRoomRequest.builder()
                .partyPostNo(savedPost.getPartyPostNo())
                .roomName(savedPost.getMovie() + " 모집 채팅방")
                .roomType(RoomType.PARTY)
                .build();
        ChatRoomResponse chatRoom = chatRoomService.createChatRoom(chatRoomRequest, dto.getMemberId());

        chatRoomService.joinChatRoom(chatRoom.getChatRoomId(), dto.getMemberId());

        ChatMessageRequest welcomeMsg = ChatMessageRequest.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .messageText("🎬 모집글 채팅방이 생성되었습니다! 파티 멤버들과 대화를 나눠보세요.")
                .build();
        chatMessageService.sendMessage(welcomeMsg, "SYSTEM");

        return toDto(savedPost);
    }

    private void validateMemberExists(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private void validatePartyPostRequest(PartyPostRequest dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
        if (dto.getMovie() == null || dto.getMovie().isBlank()) {
            throw new IllegalArgumentException("영화 제목은 필수입니다.");
        }
        if (dto.getPartyDeadline() == null || dto.getPartyDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("마감일은 현재보다 이후여야 합니다.");
        }
        if (dto.getTheaterId() == null) {
            throw new IllegalArgumentException("영화관 ID는 필수입니다.");
        }
    }

    @Override
    public Map<Integer, Long> countPartyPostsByTheater() {
        LocalDateTime now = LocalDateTime.now();
        List<Object[]> results = partyPostRepository.countPostsGroupedByTheater(now);
        return results.stream().collect(Collectors.toMap(
            r -> (Integer) r[0],
            r -> (Long) r[1]
        ));
    }

    @Override
    public PartyResponse updatePartyPost(Integer partyPostNo, PartyPostRequest dto) {
        PartyPost post = partyPostRepository.findById(partyPostNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "수정할 모집글이 존재하지 않습니다."));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setMovie(dto.getMovie());
        post.setPartyDeadline(dto.getPartyDeadline());
        post.setPartyLimit(dto.getPartyLimit());
        post.setGender(dto.getGender());
        post.setAgeGroupsMask(dto.getAgeGroupsMask());

        return toDto(partyPostRepository.save(post));
    }

    @Override
    public void deletePartyPost(Integer partyPostNo) {
        if (!partyPostRepository.existsById(partyPostNo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 모집글이 존재하지 않습니다.");
        }
        partyPostRepository.deleteById(partyPostNo);
    }

    private PartyResponse toDto(PartyPost post) {
        String theaterName = theaterRepository.findById(post.getTheaterId())
                .map(t -> t.getName())
                .orElse("알 수 없음");

        Integer chatRoomId = chatRoomService.getAllActiveRooms().stream()
                .filter(r -> r.getPartyPostNo() != null && r.getPartyPostNo().equals(post.getPartyPostNo()))
                .map(ChatRoomResponse::getChatRoomId)
                .findFirst()
                .orElse(null);

        return PartyResponse.builder()
                .memberId(post.getMemberId())
                .partyPostNo(post.getPartyPostNo())
                .nickname(post.getNickname())
                .movie(post.getMovie())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .partyDeadline(post.getPartyDeadline())
                .views(post.getViews())
                .theaterId(post.getTheaterId())
                .theaterName(theaterName)
                .partyLimit(post.getPartyLimit())
                .gender(post.getGender())
                .ageGroupsMask(post.getAgeGroupsMask())
                .chatRoomId(chatRoomId)
                .build();
    }

    private PartyPost toEntity(PartyPostRequest dto) {
        return PartyPost.builder()
                .memberId(dto.getMemberId())
                .nickname(dto.getNickname())
                .movie(dto.getMovie())
                .title(dto.getTitle())
                .content(dto.getContent())
                .partyDeadline(dto.getPartyDeadline())
                .createdAt(LocalDateTime.now())
                .views(0)
                .theaterId(dto.getTheaterId())
                .partyLimit(dto.getPartyLimit())
                .gender(dto.getGender())
                .ageGroupsMask(dto.getAgeGroupsMask())
                .build();
    }
}
