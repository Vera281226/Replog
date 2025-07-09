import React, { useEffect, useState } from 'react';
import axios from '../../error/api/interceptor';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation } from 'swiper/modules';

import MovieCard from './MovieCard'; // ✅ 카드 UI 공통 컴포넌트

import 'swiper/css';
import 'swiper/css/navigation';

/**
 * NowPlayingSection 컴포넌트
 * -------------------------------------------------------------
 * ✅ 현재 상영 중인 영화 슬라이더 섹션
 * ✅ 백엔드 API: GET /api/index/now-playing
 * ✅ MovieCard 컴포넌트 재사용
 * ✅ Swiper 내장 navigation 사용 (버튼 정상 동작 보장)
 * ✅ Disney+ / Netflix 로고 조건부 표시
 * -------------------------------------------------------------
 */
const NowPlayingSection = () => {
  const [movies, setMovies] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchNowPlaying = async () => {
      try {
        const res = await axios.get('/index/now-playing');
        console.log('🎬 현재 상영 중 영화:', res.data);
        setMovies(res.data);
      } catch (err) {
        console.error('❌ 현재 상영작 불러오기 실패:', err);
        setError('영화 정보를 불러올 수 없습니다.');
      }
    };
    fetchNowPlaying();
  }, []);

  return (
      <div className="now-playing-section" style={{ position: 'relative', padding: '0 60px' }}>
        <div className="section-inner">
          <h2 className="section-title">현재 상영 중인 영화</h2>

          {error && <p className="error-message">{error}</p>}
          {!error && movies.length === 0 && <p>현재 상영 중인 영화가 없습니다.</p>}

          {!error && movies.length > 0 && (
              <Swiper
                  modules={[Navigation]}
                  slidesPerView={6}
                  slidesPerGroup={6}
                  spaceBetween={20}
                  navigation={true}
              >
                {movies.map((movie, index) => {
                  // ✅ Disney+ (337) 또는 Netflix (8) 플랫폼 ID 확인
                  const providerIds = movie.providerIds || [];
                  let platform = null;

                  if (providerIds.includes(337)) {
                    platform = 'disney';
                  } else if (providerIds.includes(8)) {
                    platform = 'netflix';
                  }

                  return (
                      <SwiperSlide key={index} className="swiper-slide" style={{ overflow: 'visible' }}>
                        <MovieCard
                            title={movie.title}
                            posterPath={movie.posterPath}
                            releaseDate={movie.releaseDate}
                            voteAverage={movie.rating}
                            contentId={movie.contentId}
                            platform={platform} // ✅ 로고 출력용
                        />
                      </SwiperSlide>
                  );
                })}
              </Swiper>
          )}
        </div>
      </div>
  );
};

export default NowPlayingSection;
