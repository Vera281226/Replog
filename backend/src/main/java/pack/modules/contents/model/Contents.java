package pack.modules.contents.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * contents 테이블과 매핑되는 JPA Entity 클래스입니다.
 * - 영화 및 TV 콘텐츠 정보를 저장
 */
@Entity
@Table(name = "contents")
@Getter
@Setter
@NoArgsConstructor
public class Contents {

    /** 기본 키(PK) - AUTO_INCREMENT */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Integer contentId;

    /** TMDB 고유 콘텐츠 ID (외부 연동용) */
    @Column(name = "tmdb_id", unique = true)
    private Integer tmdbId;

    /** 콘텐츠 제목 */
    @Column(nullable = false)
    private String title;

    /** 콘텐츠 설명(개요) */
    @Column(columnDefinition = "TEXT")
    private String overview;

    /** 평점 (예: 8.5) */
    private Float rating;

    /** 개봉일 (예: 2023-08-21) */
    @Column(name = "release_date")
    private LocalDate releaseDate;

    /** 미디어 타입 구분 (movie 또는 tv) */
    @Column(name = "media_type", nullable = false)
    private String mediaType;

    /** 포스터 이미지 경로 */
    @Column(name = "poster_path")
    private String posterPath;

    /** 배경 이미지 경로 */
    @Column(name = "backdrop_path")
    private String backdropPath;

    /** 생성 시각 (기본값: 현재 시각) */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
