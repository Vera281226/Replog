// src/index/components/UpcomingSection.js

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import MovieCard from './MovieCard';

// ✅ 실제 TMDB API 키
const API_KEY = '16a0ea8699fa78781abae853600c2b61';

const UpcomingSection = () => {
  const [movies, setMovies] = useState([]);

  // ✅ 컴포넌트 마운트 시 개봉 예정 영화 데이터 호출
  useEffect(() => {
    const fetchUpcoming = async () => {
      try {
        const response = await axios.get(
          `https://api.themoviedb.org/3/movie/upcoming?language=ko-KR&region=KR&api_key=${API_KEY}`
        );

        const upcoming = response.data.results
          .filter(movie => movie.poster_path)
          .slice(0, 10);

        setMovies(upcoming);
      } catch (error) {
        console.error('❌ 개봉 예정 영화 데이터 호출 실패:', error);
      }
    };

    fetchUpcoming();
  }, []);

  return (
    <section>
      <div className="section-inner">
        <h2 className="section-title">개봉 예정 영화</h2>

        {/* ✅ 카드 그리드 출력 */}
        <div className="card-grid">
          {movies.map((movie, index) => (
            <MovieCard
              key={movie.id}
              title={`${index + 1}. ${movie.title}`}
              posterPath={movie.poster_path}
              releaseDate={movie.release_date}
              voteAverage={movie.vote_average}
              voteCount={movie.vote_count}
            />
          ))}
        </div>
      </div>
    </section>
  );
};

export default UpcomingSection;
