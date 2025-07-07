package pack.modules.contents.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contents
 *
 * ○ contents 테이블과 매핑되는 JPA Entity 클래스입니다.
 * ○ 영화 및 TV 콘텐츠 정보를 저장합니다.
 */
@Entity
@Table(name = "contents")
@Getter
@Setter
@NoArgsConstructor
public class Contents {

    // 내부 고유 콘텐츠 ID (PK, 자동 증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Integer contentId;

    // TMDB 콘텐츠 고유 ID (외부 연동용)
    @Column(name = "tmdb_id", unique = true)
    private Integer tmdbId;

    // 콘텐츠 제목
    @Column(nullable = false)
    private String title;

    // 콘텐츠 설명(줄거리)
    @Column(columnDefinition = "TEXT")
    private String overview;

    // TMDB 기준 평점 (예: 8.5)
    private Float rating;

    // 개봉일 또는 첫 방영일 (예: 2023-08-21)
    @Column(name = "release_date")
    private LocalDate releaseDate;

    // 미디어 타입 (movie 또는 tv)
    @Column(name = "media_type", nullable = false)
    private String mediaType;

    // 포스터 이미지 경로 (/poster.jpg)
    @Column(name = "poster_path")
    private String posterPath;

    // 배경 이미지 경로 (/backdrop.jpg)
    @Column(name = "backdrop_path")
    private String backdropPath;

    // 러닝타임 (분 단위, 예: 120)
    @Column(name = "runtime")
    private Integer runtime;

    // 연령 등급 (예: "전체관람가", "15세", "18세")
    @Column(name = "age_rating")
    private String ageRating;

    // 생성 시각 (insert 시점 기준 자동 설정)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 생성 시점에 createdAt 자동 할당
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
