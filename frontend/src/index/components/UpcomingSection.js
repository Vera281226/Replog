// src/index/components/UpcomingSection.js

import React, { useEffect, useState } from 'react';
import axios from '../../error/api/interceptor';
import MovieCard from './MovieCard';

const UpcomingSection = () => {
  const [movies, setMovies] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUpcoming = async () => {
      try {
        const response = await axios.get('/index/upcoming', {
          withCredentials: false, // ✅ 인증 제거
        });
        console.log('🎬 개봉 예정 응답 데이터:', response.data);
        setMovies(response.data);
      } catch (error) {
        console.error('❌ 개봉 예정 콘텐츠 불러오기 실패:', error);
        setError('개봉 예정 콘텐츠를 불러오지 못했습니다.');
      }
    };

    fetchUpcoming();
  }, []);

  return (
      <section>
        <div className="section-inner">
          <h2 className="section-title">개봉 예정 영화</h2>
          {error && <p className="error-message">{error}</p>}
          <div className="card-grid">
            {movies.map((movie, index) => {
              if (!movie.posterPath) return null;
              return (
                  <MovieCard
                      key={`${movie.title}-${index}`} // ✅ contentId 제거
                      title={`${index + 1}. ${movie.title}`}
                      posterPath={movie.posterPath}
                      releaseDate={movie.releaseDate}
                      voteAverage={movie.rating}
                      platform="upcoming" // ✔ 플랫폼 구분용으로 유지
                      contentId={null} // ✅ 명시적으로 null
                  />
              );
            })}
          </div>
        </div>
      </section>
  );
};

export default UpcomingSection;
