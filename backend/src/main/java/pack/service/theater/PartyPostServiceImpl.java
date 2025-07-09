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

    @Transactional
    public PartyResponse createPartyPost(PartyPostRequest dto) {
        // 1. 모집글 저장
        PartyPost savedPost = partyPostRepository.save(toEntity(dto));

        // 2. 채팅방 생성
        ChatRoomResponse chatRoom = chatRoomService.createChatRoom(
            ChatRoomRequest.builder()
                .partyPostNo(savedPost.getPartyPostNo())
                .roomName(savedPost.getMovie() + " 모집 채팅방")
                .roomType(RoomType.PARTY)
                .build(),
            dto.getMemberId()
        );

        // 3. 환영 메시지 전송 (동일 트랜잭션 내)
        chatMessageService.sendMessage(
            ChatMessageRequest.builder()
                .chatRoomId(chatRoom.getChatRoomId())
                .messageText("채팅방이 생성되었습니다!")
                .build(),
            dto.getMemberId()
        );

        return toDto(savedPost);
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
    public List<PartyResponse> getPartyPostsByMemberId(String memberId) {
        List<PartyPost> posts = partyPostRepository.findByMemberIdOrderByCreatedAtDesc(memberId);

        return posts.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
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

    @Override
    @Transactional
    public void deletePartyPost(Integer partyPostNo) {
        PartyPost partyPost = partyPostRepository.findById(partyPostNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "삭제할 모집글이 존재하지 않습니다."));
        
        // 연결된 채팅방 찾기
        try {
            ChatRoomResponse chatRoom = chatRoomService.getRoomByPartyPostNo(partyPostNo);
            if (chatRoom != null) {
                // 모든 참가자 제거
                chatRoomService.kickAllParticipants(chatRoom.getChatRoomId());
                // 채팅방 비활성화
                chatRoomService.deleteChatRoom(chatRoom.getChatRoomId(), partyPost.getMemberId());
            }
        } catch (Exception e) {
            // 채팅방이 없는 경우 무시
        }
        
        // 모집글 삭제
        partyPostRepository.deleteById(partyPostNo);
    }
}
