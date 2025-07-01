// package pack.index.repository;
//
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;
// import pack.index.dto.IndexHotReviewResponse;
// import pack.review.entity.Review;
//
// import java.util.List;
//
// /**
//  * IndexReviewRepository 인터페이스
//  * - index.html에서 사용하는 리뷰 목록 조회용 쿼리 정의 (JPQL 기반)
//  * - review, member, contents 테이블을 조인하여 인기 리뷰 목록 반환
//  */
// @Repository
// public interface IndexReviewRepository extends JpaRepository<Review, Integer> {
//
//     /**
//      * 지금 뜨는 리뷰 목록을 반환 (최신순 10개)
//      * - review + member + contents 테이블을 JOIN
//      * - IndexHotReviewResponse DTO 생성자 기반 매핑
//      *
//      * @return IndexHotReviewResponse 리스트
//      */
//     @Query("""
//         SELECT new pack.index.dto.IndexHotReviewResponse(
//             r.num,
//             c.title,
//             m.nickname,
//             r.rating,
//             r.cont,
//             FUNCTION('DATE_FORMAT', r.createdAt, '%Y-%m-%d')
//         )
//         FROM pack.review.entity.Review r
//         JOIN pack.model.member.Member m ON r.memberId = m.memberId
//         JOIN pack.modules.contents.model.Contents c ON r.contentId = c.contentId
//         ORDER BY r.createdAt DESC
//         """)
//     List<IndexHotReviewResponse> findHotReviews();
// }
