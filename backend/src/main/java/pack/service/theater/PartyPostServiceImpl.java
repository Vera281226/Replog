package pack.service.theater;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pack.dto.theater.PartyResponse;
import pack.dto.theater.PartyPostRequest;
import pack.model.theater.PartyPost;
import pack.repository.theater.PartyPostRepository;
import pack.repository.theater.TheaterRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyPostServiceImpl implements PartyPostService {

    private final PartyPostRepository partyPostRepository;
    private final TheaterRepository theaterRepository;

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
    public List<PartyResponse> getFilteredPartyPosts(List<Integer> theaterIds, String start, String end) {
        LocalDateTime now = LocalDateTime.now();
        boolean hasDateFilter = start != null && !start.isBlank() && end != null && !end.isBlank();

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        if (hasDateFilter) {
            startDateTime = LocalDate.parse(start).atStartOfDay();
            endDateTime = LocalDate.parse(end).atTime(23, 59, 59);
        }

        List<PartyPost> posts;

        if (theaterIds == null || theaterIds.isEmpty()) {
            if (hasDateFilter) {
                posts = partyPostRepository
                        .findByIsHiddenFalseAndPartyDeadlineBetweenAndPartyDeadlineAfter(
                                startDateTime, endDateTime, now);
            } else {
                posts = partyPostRepository
                        .findByIsHiddenFalseAndPartyDeadlineAfter(now);
            }
        } else {
            if (hasDateFilter) {
                posts = partyPostRepository
                        .findByTheaterIdInAndIsHiddenFalseAndPartyDeadlineBetweenAndPartyDeadlineAfter(
                                theaterIds, startDateTime, endDateTime, now);
            } else {
                posts = partyPostRepository
                        .findByTheaterIdInAndIsHiddenFalseAndPartyDeadlineAfter(theaterIds, now);
            }
        }

        return posts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void createPartyPost(PartyPostRequest dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 필수입니다.");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 필수입니다.");
        }
        if (dto.getMovie() == null || dto.getMovie().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "영화 제목은 필수입니다.");
        }
        if (dto.getPartyDeadline() == null || dto.getPartyDeadline().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "마감일은 현재보다 이후여야 합니다.");
        }
        if (dto.getTheaterId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "영화관 ID는 필수입니다.");
        }

        PartyPost post = toEntity(dto);
        partyPostRepository.save(post);
    }

    @Override
    public Map<Integer, Long> countPartyPostsByTheater() {
        List<Object[]> results = partyPostRepository.countPostsGroupedByTheater();
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

        return PartyResponse.builder()
                .id(post.getId())
                .partyPostNo(post.getPartyPostNo())
                .nickname(post.getNickname())
                .movie(post.getMovie())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .partyDeadline(post.getPartyDeadline())
                .isTerminated(post.getIsTerminated())
                .isHidden(post.getIsHidden())
                .views(post.getViews())
                .theaterId(post.getTheaterId())
                .theaterName(theaterName)  // ✅ 추가
                .partyLimit(post.getPartyLimit())
                .gender(post.getGender())
                .ageGroupsMask(post.getAgeGroupsMask())
                .build();
    }


    private PartyPost toEntity(PartyPostRequest dto) {
        return PartyPost.builder()
                .id(dto.getId())
                .nickname(dto.getNickname())
                .movie(dto.getMovie())
                .title(dto.getTitle())
                .content(dto.getContent())
                .partyDeadline(dto.getPartyDeadline())
                .createdAt(LocalDateTime.now())
                .isTerminated(false)
                .isHidden(false)
                .views(0)
                .theaterId(dto.getTheaterId())
                .partyLimit(dto.getPartyLimit())
                .gender(dto.getGender())
                .ageGroupsMask(dto.getAgeGroupsMask())
                .build();
    }
}
