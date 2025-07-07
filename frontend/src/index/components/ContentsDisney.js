import React, { useEffect, useState } from 'react';
import axios from '../../error/api/interceptor';
import MovieCard from './MovieCard'; // 공통 카드 컴포넌트 import

/**
 * ContentsDisney 컴포넌트
 * - 디즈니+ 인기 콘텐츠를 백엔드 API를 통해 출력
 * - 백엔드 API: GET /api/index/disney
 */
const ContentsDisney = () => {
  const [movies, setMovies] = useState([]);
  const [error, setError] = useState(null); // 에러 상태 관리

  // ✅ 백엔드 API로 디즈니+ 콘텐츠 가져오기
  useEffect(() => {
    const fetchDisneyContent = async () => {
      try {
        const response = await axios.get('/index/disney');
        console.log("🎬 디즈니 응답 데이터:", response.data);
        setMovies(response.data);
      } catch (error) {
        console.error('❌ 디즈니+ 콘텐츠 불러오기 실패:', error);
        setError('콘텐츠를 불러오지 못했습니다.');
      }
    };

    fetchDisneyContent();
  }, []);

  return (
      <section>
        <div className="section-inner">
          <h2 className="section-title">디즈니+ 인기 콘텐츠</h2>

          {/* 오류 메시지 출력 */}
          {error && <p className="error-message">{error}</p>}

          {/* ✅ 카드 그리드 */}
          <div className="card-grid">
            {movies.map((movie, index) => {
              if (!movie.posterPath) return null; // ❗포스터 없는 항목 제외

              return (
                  <MovieCard
                      key={`${movie.contentId}-${index}`} // ✅ 고유 key
                      title={`${index + 1}. ${movie.title}`}
                      posterPath={movie.posterPath}
                      releaseDate={movie.releaseDate}
                      voteAverage={movie.rating}
                      voteCount={movie.voteCount}
                      platform="disney" // ✅ 디즈니 로고 출력용
                  />
              );
            })}
          </div>
        </div>
      </section>
  );
};

export default ContentsDisney;
