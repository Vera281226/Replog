import React from 'react';
import '../css/MovieCard.css';

/**
 * ✅ MovieCard 컴포넌트
 * - TMDB 콘텐츠 1개를 카드 형태로 보여줌
 * - props로 전달된 제목, 포스터, 개봉일, 별점 정보를 출력
 * - platform 값이 있으면 로고를 포스터 하단에 오버레이
 */
const MovieCard = ({ title, posterPath, releaseDate, voteAverage, voteCount, platform }) => {
  // ✅ 플랫폼 로고 이미지 경로 설정
  const getPlatformLogo = () => {
    if (platform === 'netflix') {
      return '/images/netflix-logo.png'; // public/images/netflix-logo.png 위치에 저장
    } else if (platform === 'disney') {
      return '/images/disney-logo.png'; // public/images/disney-logo.png 위치에 저장
    }
    return null;
  };

  return (
      <div className="movie-card">
        <div className="poster-wrapper">
          <img
              className="poster"
              src={`https://image.tmdb.org/t/p/w500${posterPath}`}
              alt={title}
          />
          {platform && (
              <img
                  className="platform-logo"
                  src={getPlatformLogo()}
                  alt={platform}
              />
          )}
        </div>

        <div className="movie-info">
          <div className="movie-title">{title}</div>
          <div className="movie-sub">{releaseDate?.slice(0, 4)}</div>
          <div className="movie-rating">
            평균 ⭐ {voteAverage?.toFixed(1)} / 참여 {voteCount}명
          </div>
        </div>
      </div>
  );
};

export default MovieCard;
