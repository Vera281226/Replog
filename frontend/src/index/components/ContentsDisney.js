// src/index/components/ContentsDisney.js

import React, { useEffect, useState } from 'react';
import axios from '../../error/api/interceptor';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation } from 'swiper/modules';

import MovieCard from './MovieCard'; // ✅ 공통 카드 컴포넌트

import 'swiper/css';
import 'swiper/css/navigation';

/**
 * ContentsDisney 컴포넌트
 * -------------------------------------------------------------------
 * ✅ 디즈니+ 인기 콘텐츠 슬라이더 섹션
 * ✅ 백엔드 API: GET /api/index/disney
 * ✅ MovieCard 컴포넌트 재사용
 * ✅ Swiper 내장 navigation 사용 (< > 버튼 포함)
 * -------------------------------------------------------------------
 */
const ContentsDisney = () => {
  // ⬛ 콘텐츠 목록 상태
  const [movies, setMovies] = useState([]);

  // ⬛ 에러 상태
  const [error, setError] = useState(null);

  // -------------------------------------------------------------
  // ✅ 디즈니+ 콘텐츠 백엔드에서 호출
  // -------------------------------------------------------------
  useEffect(() => {
    const fetchDisneyContent = async () => {
      try {
        const response = await axios.get('/index/disney');
        console.log('🎬 디즈니 응답 데이터:', response.data);
        setMovies(response.data);
      } catch (error) {
        console.error('❌ 디즈니+ 콘텐츠 불러오기 실패:', error);
        setError('콘텐츠를 불러오지 못했습니다.');
      }
    };

    fetchDisneyContent();
  }, []);

  // -------------------------------------------------------------
  // ✅ 렌더링
  // -------------------------------------------------------------
  return (
      <div className="contents-disney-section" style={{ position: 'relative', padding: '0 60px' }}>
        <div className="section-inner">

          {/* ✅ 섹션 제목 */}
          <h2 className="section-title">디즈니+ 인기 콘텐츠</h2>

          {/* ✅ 에러 메시지 출력 */}
          {error && <p className="error-message">{error}</p>}

          {/* ✅ 콘텐츠 없음 안내 */}
          {!error && movies.length === 0 && (
              <p>콘텐츠를 불러오지 못했습니다.</p>
          )}

          {/* ✅ 슬라이더 출력 */}
          {!error && movies.length > 0 && (
              <Swiper
                  modules={[Navigation]}
                  slidesPerView={6}               // ✅ 한 화면에 6개
                  slidesPerGroup={6}              // ✅ 6개씩 이동
                  spaceBetween={20}
                  navigation={true}               // ✅ < > 버튼 사용
              >
                {movies.map((movie, index) => {
                  if (!movie.posterPath) return null;

                  return (
                      <SwiperSlide key={`${movie.contentId}-${index}`}>
                        <MovieCard
                            title={`${index + 1}. ${movie.title}`}
                            posterPath={movie.posterPath}
                            releaseDate={movie.releaseDate}
                            voteAverage={movie.rating}
                            voteCount={movie.voteCount}
                            platform="disney" // ✅ 디즈니 플랫폼 로고 출력용
                            contentId={movie.contentId}
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

export default ContentsDisney;
