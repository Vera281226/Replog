package pack.repository.theater;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pack.model.theater.PartyPost;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartyPostRepository extends JpaRepository<PartyPost, Integer> {

	// 전체 극장 + 날짜 필터 O
	List<PartyPost> findByIsHiddenFalseAndPartyDeadlineBetweenAndPartyDeadlineAfter(
	        LocalDateTime start, LocalDateTime end, LocalDateTime now);

	// 전체 극장 + 날짜 필터 X
	List<PartyPost> findByIsHiddenFalseAndPartyDeadlineAfter(LocalDateTime now);

	// 선택된 극장 + 날짜 필터 O
	List<PartyPost> findByTheaterIdInAndIsHiddenFalseAndPartyDeadlineBetweenAndPartyDeadlineAfter(
	        List<Integer> theaterIds, LocalDateTime start, LocalDateTime end, LocalDateTime now);

	// 선택된 극장 + 날짜 필터 X
	List<PartyPost> findByTheaterIdInAndIsHiddenFalseAndPartyDeadlineAfter(
	        List<Integer> theaterIds, LocalDateTime now);
	
    // 영화관별 모집글 개수 (isHidden = false 기준)
    @Query("SELECT p.theaterId, COUNT(p) FROM PartyPost p WHERE p.isHidden = false GROUP BY p.theaterId")
    List<Object[]> countPostsGroupedByTheater();
}