	package pack.service.theater;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import pack.dto.theater.PartyResponse;
import pack.dto.chat.ChatRoomRequest;
import pack.dto.theater.PartyPostRequest;
import pack.model.chat.ChatMessage;
import pack.model.chat.ChatParticipant;
import pack.model.chat.ChatRoom;
import pack.model.chat.ChatRoom.RoomType;
import pack.model.theater.PartyPost;
import pack.repository.chat.ChatMessageRepository;
import pack.repository.chat.ChatParticipantRepository;
import pack.repository.chat.ChatRoomRepository;
import pack.repository.member.MemberRepository;
import pack.repository.theater.PartyPostRepository;
import pack.repository.theater.TheaterRepository;
import pack.service.chat.ChatService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyPostServiceImpl implements PartyPostService {

    private final PartyPostRepository partyPostRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final TheaterRepository theaterRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MemberRepository memberRepository;

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
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())) // 최신 등록순 정렬
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PartyResponse createPartyPost(PartyPostRequest dto) {
        // ✅ 입력값 검증 강화
        validatePartyPostRequest(dto);
        validateMemberExists(dto.getMemberId()); // 추가 검증
        
        // 모집글 저장
        PartyPost post = toEntity(dto);
        PartyPost savedPost = partyPostRepository.save(post);
        
        // ✅ 채팅방 생성 및 작성자 자동 참가
        ChatRoom chatRoom = createPartyCharRoom(savedPost);
        addCreatorToPartyRoom(chatRoom.getChatRoomId(), dto.getMemberId());
        
        return toDto(savedPost);
    }
    
    private void addCreatorToPartyRoom(Integer chatRoomId, String memberId) {
        try {
            // ChatParticipant를 직접 생성하여 추가
            ChatParticipant participant = ChatParticipant.builder()
                .chatRoomId(chatRoomId)
                .memberId(memberId)
                .joinedAt(LocalDateTime.now())
                .build();
            
            chatParticipantRepository.save(participant);
            
            // ✅ 환영 메시지 추가
            addWelcomeMessage(chatRoomId);
            
        } catch (Exception e) {
            // 실패해도 모집글 생성은 계속 진행
        }
    }
    
    private void addWelcomeMessage(Integer chatRoomId) {
        try {
            ChatMessage welcomeMessage = ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId("SYSTEM")
                .messageText("🎬 모집글 채팅방이 생성되었습니다! 파티 멤버들과 대화를 나눠보세요.")
                .sentAt(LocalDateTime.now())
                .build();
            
            chatMessageRepository.save(welcomeMessage);
            
        } catch (Exception e) {

        }
    }

	private void validateMemberExists(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }
        
        // member 테이블에 해당 ID가 존재하는지 확인
        // MemberRepository가 있다면 사용, 없다면 직접 쿼리
        try {
             memberRepository.findById(memberId)
                 .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        } catch (Exception e) {
            throw new IllegalArgumentException("사용자 정보를 확인할 수 없습니다.");
        }
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
    
	private ChatRoom createPartyCharRoom(PartyPost post) {
	    ChatRoom chatRoom = ChatRoom.builder()
	        .partyPostNo(post.getPartyPostNo())
	        .roomName(post.getMovie() + " 모집 채팅방")  // 이름 개선
	        .roomType(ChatRoom.RoomType.PARTY)
	        .isActive(true)
	        .build();
	    
	    ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
	    
	    return savedRoom;
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

        Integer chatRoomId = chatRoomRepository.findByPartyPostNo(post.getPartyPostNo())
                .map(ChatRoom::getChatRoomId)
                .orElse(null); // 존재하지 않을 수도 있으므로 null 처리

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
                .chatRoomId(chatRoomId) // ✅ 추가됨
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
