import React, { useEffect, useState } from 'react';
import axios from 'axios';
import TrailerCard from './TrailerCard'; // ✅ 예고편 카드 컴포넌트 import

// ✅ TMDB API 키
const API_KEY = process.env.REACT_APP_TMDB_API_KEY;

/**
 * ✅ TrailerSection 컴포넌트
 * - TMDB 인기 영화 중 예고편이 있는 항목을 찾아 최대 6개 표시
 * - 각 예고편은 TrailerCard를 사용해 썸네일 카드 형태로 출력
 */
const TrailerSection = () => {
  const [trailers, setTrailers] = useState([]);

  useEffect(() => {
    const fetchTrailers = async () => {
      try {
        // ✅ 인기 영화 10개 가져오기 (예고편 확률 높이기 위해 10개로 설정)
        const movieRes = await axios.get(
          `https://api.themoviedb.org/3/movie/popular?language=ko-KR&region=KR&api_key=${API_KEY}`
        );

        const movies = movieRes.data.results.slice(0, 10); // 상위 10개 영화만 시도
        const trailerList = [];

        for (const movie of movies) {
          // ✅ 각 영화에 대해 예고편 조회
          const videoRes = await axios.get(
            `https://api.themoviedb.org/3/movie/${movie.id}/videos?language=ko-KR&api_key=${API_KEY}`
          );

          // ✅ 예고편(Trailer) 중 YouTube 영상만 선택
          const trailer = videoRes.data.results.find(
            (v) => v.type === 'Trailer' && v.site === 'YouTube'
          );

          // ✅ 예고편 있으면 리스트에 추가
          if (trailer) {
            trailerList.push({
              id: movie.id,
              title: movie.title,
              description: movie.overview,
              videoKey: trailer.key,
            });
          }

          // ✅ 최대 6개만 수집
          if (trailerList.length === 6) break;
        }

        setTrailers(trailerList);
      } catch (error) {
        console.error('❌ 예고편 불러오기 실패:', error);
      }
    };

    fetchTrailers();
  }, []);

  return (
    <section>
      <div className="section-inner">
        <h2 className="section-title">추천 예고편</h2>

        {/* ✅ 예고편 카드 6개 그리드로 출력 */}
        <div className="card-grid">
          {trailers.map((item) => (
            <TrailerCard
              key={item.id}
              title={item.title}
              description={item.description}
              videoKey={item.videoKey}
            />
          ))}
        </div>
      </div>
    </section>
  );
};

export default TrailerSection;
