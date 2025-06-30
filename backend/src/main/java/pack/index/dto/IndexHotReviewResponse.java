package pack.index.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * IndexHotReviewResponse DTO
 * - index.html에서 "지금 뜨는 리뷰" 섹션에 표시할 데이터 전용 DTO
 * - review + member + contents 테이블의 일부 필드 사용
 *
 * ⚠ 현재 리뷰 기능은 임시 테스트용으로만 작성됨 (추후 review 엔티티 연동 필요)
 */
@Getter
@Setter
public class IndexHotReviewResponse {

    /** 리뷰 ID */
    private int reviewId;

    /** 콘텐츠 제목 */
    private String contentTitle;

    /** 작성자 닉네임 */
    private String nickname;

    /** 리뷰 평점 */
    private int rating;

    /** 리뷰 내용 */
    private String cont;

    /** 작성일 */
    private String createdAt;
}
