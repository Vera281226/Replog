// src/index/components/NowPlayingSection.jsx

import React, { useEffect, useState } from 'react';
import axios from '../../error/api/interceptor';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation } from 'swiper/modules';

import 'swiper/css';
import 'swiper/css/navigation';

/**
 * NowPlayingSection 컴포넌트 (camelCase 응답 필드 대응 버전)
 */
const NowPlayingSection = () => {
  const [movies, setMovies] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchNowPlaying = async () => {
      try {
        const res = await axios.get('/index/now-playing');
        setMovies(res.data);
      } catch (err) {
        console.error('현재 상영작 데이터를 불러오는 데 실패했습니다:', err);
        setError('영화 정보를 불러올 수 없습니다.');
      }
    };

    fetchNowPlaying();
  }, []);

  return (
      <div className="now-playing-section">
        <div className="section-inner">
          {error && <p className="error-message">{error}</p>}
          {!error && movies.length === 0 && <p>현재 상영 중인 영화가 없습니다.</p>}
          {!error && movies.length > 0 && (
              <Swiper
                  modules={[Navigation]}
                  slidesPerView={5}
                  spaceBetween={20}
                  navigation
              >
                {movies.map((movie, index) => (
                    <SwiperSlide key={index}>
                      <div className="movie-card">
                        <img
                            src={`https://image.tmdb.org/t/p/w500${movie.posterPath}`}
                            alt={movie.title || '영화 포스터'}
                            className="poster"
                            onError={(e) => {
                              e.target.onerror = null;
                              e.target.src = '/assets/default-poster.png';
                            }}
                        />
                        <div className="movie-info">
                          <div className="movie-title">{index + 1}. {movie.title}</div>
                          <div className="movie-sub">{movie.releaseDate?.slice(0, 4)}</div>
                          <div className="movie-rating">
                            평균 ⭐ {movie.rating?.toFixed(1)}점
                          </div>
                        </div>
                      </div>
                    </SwiperSlide>
                ))}
              </Swiper>
          )}
        </div>
      </div>
  );
};

export default NowPlayingSection;
