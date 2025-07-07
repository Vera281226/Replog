// src/index/components/ContentsNetflix.js

import React, { useEffect, useState } from 'react';
import axios from '../../error/api/interceptor';
import MovieCard from './MovieCard'; // 공통 카드 컴포넌트 import

/**
 * ContentsNetflix 컴포넌트
 * --------------------------------------------------------------------
 * ○ 넷플릭스 인기 콘텐츠 출력
 * ○ 백엔드 API: GET /api/index/netflix
 * --------------------------------------------------------------------
 */
const ContentsNetflix = () => {
  const [movies, setMovies] = useState([]);
  const [error, setError] = useState(null);

  // ✅ 넷플릭스 인기 콘텐츠 호출
  useEffect(() => {
    const fetchNetflixContent = async () => {
      try {
        const response = await axios.get('/index/netflix');
        console.log('🎬 넷플릭스 응답 데이터:', response.data);
        setMovies(response.data);
      } catch (error) {
        console.error('❌ 넷플릭스 콘텐츠 불러오기 실패:', error);
        setError('콘텐츠를 불러오지 못했습니다.');
      }
    };

    fetchNetflixContent();
  }, []);

  return (
      <section>
        <div className="section-inner">
          <h2 className="section-title">넷플릭스 인기 콘텐츠</h2>

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
                      voteCount={movie.voteCount}
                      platform="netflix" // ✅ 플랫폼 로고 출력용
                  />
              );
            })}
          </div>
        </div>
      </section>
  );
};

export default ContentsNetflix;
