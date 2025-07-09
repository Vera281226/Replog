import React from 'react';
import '../css/MovieCard.css';

/**
 * ✅ MovieCard 컴포넌트 (리뷰 패딩 충돌 완전 방지 버전)
 * ------------------------------------------------------------
 * ○ 외부 padding 영향 안 받도록 .card-wrapper로 완전 격리
 * ○ 포스터 + 플랫폼 로고 + 제목 + 연도 + 평점 표시
 * ------------------------------------------------------------
 */
const MovieCard = ({ title, posterPath, releaseDate, voteAverage, platform }) => {
    const getPlatformLogo = () => {
        if (platform === 'netflix') return '/images/netflix-logo.png';
        if (platform === 'disney') return '/images/disney-logo.png';
        return null;
    };

    return (
        <div className="card-wrapper">
            <div className="movie-card">
                <div className="poster-wrapper">
                    <img
                        className="poster"
                        src={`https://image.tmdb.org/t/p/w500${posterPath}`}
                        alt={title}
                        onError={(e) => {
                            e.target.onerror = null;
                            e.target.src = '/assets/default-poster.png';
                        }}
                    />
                    {getPlatformLogo() && (
                        <img
                            className="platform-logo"
                            src={getPlatformLogo()}
                            alt={platform}
                        />
                    )}
                </div>

                <div className="movie-info">
                    <div className="movie-title">{title}</div>
                    <div className="movie-release-year">{releaseDate?.slice(0, 4)}</div>
                    <div className="movie-rating">평점: {voteAverage?.toFixed(1)}점</div>
                </div>
            </div>
        </div>
    );
};

export default MovieCard;
