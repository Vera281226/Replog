package pack.repository.theater;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pack.model.theater.PartyPost;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartyPostRepository extends JpaRepository<PartyPost, Integer> {

    // 전체 극장 + 날짜 필터 O
    List<PartyPost> findByPartyDeadlineBetweenAndPartyDeadlineAfter(
            LocalDateTime start, LocalDateTime end, LocalDateTime now);

    // 전체 극장 + 날짜 필터 X
    List<PartyPost> findByPartyDeadlineAfter(LocalDateTime now);

    // 선택된 극장 + 날짜 필터 O
    List<PartyPost> findByTheaterIdInAndPartyDeadlineBetweenAndPartyDeadlineAfter(
            List<Integer> theaterIds, LocalDateTime start, LocalDateTime end, LocalDateTime now);

    // 선택된 극장 + 날짜 필터 X
    List<PartyPost> findByTheaterIdInAndPartyDeadlineAfter(
            List<Integer> theaterIds, LocalDateTime now);

    // 선택된 극장 + 날짜 + 영화명
    List<PartyPost> findByTheaterIdInAndPartyDeadlineBetweenAndPartyDeadlineAfterAndMovieContainingIgnoreCase(
            List<Integer> theaterIds, LocalDateTime start, LocalDateTime end, LocalDateTime now, String movieKeyword);

    // 선택된 극장 + 영화명 (날짜 필터 X)
    List<PartyPost> findByTheaterIdInAndPartyDeadlineAfterAndMovieContainingIgnoreCase(
            List<Integer> theaterIds, LocalDateTime now, String movieKeyword);

    // 전체 극장 + 날짜 + 영화명
    List<PartyPost> findByPartyDeadlineBetweenAndPartyDeadlineAfterAndMovieContainingIgnoreCase(
            LocalDateTime start, LocalDateTime end, LocalDateTime now, String movieKeyword);

    // 전체 극장 + 영화명 (날짜 필터 X)
    List<PartyPost> findByPartyDeadlineAfterAndMovieContainingIgnoreCase(
            LocalDateTime now, String movieKeyword);

    // 영화관별 모집글 개수
    @Query("SELECT p.theaterId, COUNT(p) FROM PartyPost p " +
           "WHERE p.partyDeadline > :now " +
           "GROUP BY p.theaterId")
    List<Object[]> countPostsGroupedByTheater(@Param("now") LocalDateTime now);
}