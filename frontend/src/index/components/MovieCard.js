import React from 'react';
import { Link } from 'react-router-dom';
import '../css/MovieCard.css';

/**
 * ✅ MovieCard 컴포넌트
 * -------------------------------------------------------------------
 * ○ TMDB 콘텐츠 1개 카드 UI
 * ○ 포스터 + 플랫폼 로고 이미지 + 제목 · 연도 + 평점 구성
 * ○ 클릭 시 해당 컨텐츠의 리뷰 페이지로 이동
 * -------------------------------------------------------------------
 */
const MovieCard = ({ title, posterPath, releaseDate, voteAverage, platform, contentId }) => {
    // ✅ 플랫폼 로고 이미지 경로 반환
    const getPlatformLogo = () => {
        if (platform === 'netflix') return '/images/netflix-logo.png';
        if (platform === 'disney') return '/images/disney-logo.png';
        return null;
    };

    return (
        <Link 
            to={`/contents/${contentId}/reviews`} 
            style={{ textDecoration: 'none', color: 'inherit' }}
        >
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
                    {/* ✅ 플랫폼 로고 이미지 (오른쪽 위) */}
                    {getPlatformLogo() && (
                        <img
                            className="platform-logo"
                            src={getPlatformLogo()}
                            alt={platform}
                        />
                    )}
                </div>

                <div className="movie-info">
                    <div className="movie-title">
                        {title} · {releaseDate?.slice(0, 4)}
                    </div>
                    <div className="movie-rating">
                        평점: {voteAverage?.toFixed(1)}점
                    </div>
                </div>
            </div>
        </Link>
    );
};

export default MovieCard;
