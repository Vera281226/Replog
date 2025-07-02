// src/index/components/NowPlayingSection.js

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation } from 'swiper/modules';

import 'swiper/css';
import 'swiper/css/navigation';

/* TMDB API 키 */
const API_KEY = process.env.REACT_APP_TMDB_API_KEY;

const NowPlayingSection = () => {
  const [movies, setMovies] = useState([]);

  /* 컴포넌트 마운트 시 TMDB에서 현재 상영작 가져오기 */
  useEffect(() => {
    const fetchNowPlaying = async () => {
      try {
        const res = await axios.get(
          `https://api.themoviedb.org/3/movie/now_playing?language=ko-KR&page=1&region=KR&api_key=${API_KEY}`
        );

        const sorted = res.data.results
          .slice(0, 20)
          .sort((a, b) => b.popularity - a.popularity);

        setMovies(sorted);
      } catch (error) {
        console.error('TMDB 현재 상영작 호출 실패:', error);
      }
    };

    fetchNowPlaying();
  }, []);

  return (
    <div className="now-playing-section">
      <div className="section-inner">
        <Swiper
          modules={[Navigation]}
          slidesPerView={5}
          spaceBetween={20}
          navigation
        >
          {movies.map((movie, index) => (
            <SwiperSlide key={movie.id}>
              <div className="movie-card">
                <img
                  src={`https://image.tmdb.org/t/p/w500${movie.poster_path}`}
                  alt={movie.title || '영화 포스터'}
                  className="poster"
                />
                <div className="movie-info">
                  <div className="movie-title">{index + 1}. {movie.title}</div>
                  <div className="movie-sub">{movie.release_date?.slice(0, 4)}</div>
                  <div className="movie-rating">
                    평균 ⭐ {movie.vote_average?.toFixed(1)} / 참여 {movie.vote_count}명
                  </div>
                </div>
              </div>
            </SwiperSlide>
          ))}
        </Swiper>
      </div>
    </div>
  );
};

export default NowPlayingSection;
