// package pack.index.repository;
//
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import pack.index.dto.IndexHotReviewResponse;
// import pack.model.Review;
//
// import java.util.List;
//
// /**
//  * IndexReviewRepository 인터페이스
//  * - index.html에서 사용하는 리뷰 목록 조회용 쿼리만 정의
//  */
// public interface IndexReviewRepository extends JpaRepository<Review, Integer> {
//
//     /**
//      * 지금 뜨는 리뷰 목록을 반환
//      * - review, member, contents 테이블을 JOIN하여 최신순 10개
//      * - 결과는 IndexHotReviewResponse DTO에 매핑
//      */
//     @Query(value = """
//         SELECT
//             r.review_id AS reviewId,
//             c.title AS contentTitle,
//             m.nickname AS nickname,
//             r.rating AS rating,
//             r.cont AS cont,
//             DATE_FORMAT(r.created_at, '%Y-%m-%d') AS createdAt
//         FROM review r
//         JOIN member m ON r.member_id = m.member_id
//         JOIN contents c ON r.content_id = c.content_id
//         ORDER BY r.created_at DESC
//         LIMIT 10
//     """, nativeQuery = true)
//     List<IndexHotReviewResponse> findHotReviews();
// }
