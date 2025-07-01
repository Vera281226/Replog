// src/index/components/ContentsNetflix.js

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import MovieCard from './MovieCard'; // 공통 카드 컴포넌트 import

// ✅ 실제 TMDB API 키 사용
const API_KEY = '16a0ea8699fa78781abae853600c2b61';

const ContentsNetflix = () => {
  const [movies, setMovies] = useState([]);

  // ✅ 컴포넌트 마운트 시 넷플릭스 콘텐츠 호출
  useEffect(() => {
    const fetchNetflixContent = async () => {
      try {
        const response = await axios.get(
          `https://api.themoviedb.org/3/discover/movie?with_watch_providers=8&watch_region=KR&language=ko-KR&sort_by=popularity.desc&api_key=${API_KEY}`
        );

        const top10 = response.data.results.slice(0, 10); // 상위 10개만 사용
        setMovies(top10);
      } catch (error) {
        console.error('❌ 넷플릭스 콘텐츠 불러오기 실패:', error);
      }
    };

    fetchNetflixContent();
  }, []);

  return (
    <section>
      <div className="section-inner">
        <h2 className="section-title">넷플릭스 인기 콘텐츠</h2>

        {/* ✅ 슬라이더가 아닌 자연스러운 카드 나열 */}
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

export default ContentsNetflix;
