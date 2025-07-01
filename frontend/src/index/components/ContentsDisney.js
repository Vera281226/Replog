// src/index/components/ContentsDisney.js

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import MovieCard from './MovieCard'; // 공통 카드 컴포넌트 import

// ✅ TMDB API 키
const API_KEY = process.env.REACT_APP_TMDB_API_KEY;

const ContentsDisney = () => {
  const [movies, setMovies] = useState([]);

  // ✅ 디즈니+ 콘텐츠 데이터 fetch
  useEffect(() => {
    const fetchDisneyContent = async () => {
      try {
        const response = await axios.get(
          `https://api.themoviedb.org/3/discover/movie?with_watch_providers=337&watch_region=KR&language=ko-KR&sort_by=popularity.desc&api_key=${API_KEY}`
        );

        const top10 = response.data.results.slice(0, 10);
        setMovies(top10);
      } catch (error) {
        console.error('❌ 디즈니+ 콘텐츠 불러오기 실패:', error);
      }
    };

    fetchDisneyContent();
  }, []);

  return (
    <section>
      <div className="section-inner">
        <h2 className="section-title">디즈니+ 인기 콘텐츠</h2>

        {/* ✅ 카드 그리드 */}
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

export default ContentsDisney;
