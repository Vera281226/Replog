// src/index/components/UpcomingSection.js

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import MovieCard from './MovieCard'; // ✅ 공통 카드 컴포넌트 import

/**
 * UpcomingSection 컴포넌트
 * --------------------------------------------------------------------
 * ○ '개봉 예정 영화' 콘텐츠 섹션 출력
 * ○ 백엔드 API: GET /api/index/upcoming
 * ○ TMDB 직접 호출이 아닌 DB 저장된 데이터 기반으로 렌더링
 * --------------------------------------------------------------------
 */
const UpcomingSection = () => {
  const [movies, setMovies] = useState([]);      // 영화 데이터 상태
  const [error, setError] = useState(null);      // 에러 상태

  // ✅ 컴포넌트 마운트 시 개봉 예정 영화 데이터 호출
  useEffect(() => {
    const fetchUpcoming = async () => {
      try {
        const response = await axios.get('/api/index/upcoming');
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

          {/* 오류 메시지 출력 */}
          {error && <p className="error-message">{error}</p>}

          {/* ✅ 카드 리스트 출력 */}
          <div className="card-grid">
            {movies.map((movie, index) => {
              if (!movie.posterPath) return null;

              return (
                  <MovieCard
                      key={`${movie.contentId}-${index}`}
                      title={`${index + 1}. ${movie.title}`}
                      posterPath={movie.posterPath}
                      releaseDate={movie.releaseDate}
                      voteAverage={movie.rating}
                      platform="upcoming" // ✔ 플랫폼은 없지만 용도 구분용
                  />
              );
            })}
          </div>
        </div>
      </section>
  );
};

export default UpcomingSection;
