import React, { useEffect, useState } from 'react';
import axios from '../../error/api/interceptor';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation } from 'swiper/modules';

import MovieCard from './MovieCard';  // ✅ 카드 UI 공통 컴포넌트

import 'swiper/css';
import 'swiper/css/navigation';

/**
 * NowPlayingSection 컴포넌트
 * -------------------------------------------------------------
 * ✅ 현재 상영 중인 영화 슬라이더 섹션
 * ✅ 백엔드 API: GET /api/index/now-playing
 * ✅ MovieCard 컴포넌트 재사용
 * ✅ Swiper 내장 navigation 사용 (버튼 정상 동작 보장)
 * -------------------------------------------------------------
 */
const NowPlayingSection = () => {
  // ⬛ 영화 목록 상태
  const [movies, setMovies] = useState([]);

  // ⬛ 에러 상태
  const [error, setError] = useState(null);

  // -------------------------------------------------------------
  // ✅ 컴포넌트 마운트 시 영화 목록 불러오기
  // -------------------------------------------------------------
  useEffect(() => {
    const fetchNowPlaying = async () => {
      try {
        const res = await axios.get('/index/now-playing');
        setMovies(res.data);
      } catch (err) {
        console.error('❌ 현재 상영작 불러오기 실패:', err);
        setError('영화 정보를 불러올 수 없습니다.');
      }
    };
    fetchNowPlaying();
  }, []);

  // -------------------------------------------------------------
  // ✅ 렌더링
  // -------------------------------------------------------------
  return (
      <div className="now-playing-section" style={{ position: 'relative', padding: '0 60px' }}>
        <div className="section-inner">

          {/* ✅ 섹션 제목 */}
          <h2 className="section-title">현재 상영 중인 영화</h2>

          {/* ✅ 에러 메시지 출력 */}
          {error && <p className="error-message">{error}</p>}

          {/* ✅ 영화 없음 안내 */}
          {!error && movies.length === 0 && (
              <p>현재 상영 중인 영화가 없습니다.</p>
          )}

          {/* ✅ 슬라이더 출력 */}
          {!error && movies.length > 0 && (
              <Swiper
                  modules={[Navigation]}
                  slidesPerView={6}               // ✅ 한 화면에 6개 표시
                  slidesPerGroup={6}              // ✅ 6개씩 이동
                  spaceBetween={20}
                  navigation={true}               // ✅ 기본 내장 내비게이션 사용
              >
                {movies.map((movie, index) => (
                    <SwiperSlide key={index}>
                      <MovieCard
                          title={movie.title}
                          posterPath={movie.posterPath}
                          releaseDate={movie.releaseDate}
                          voteAverage={movie.rating}
                          platform={null}  // ✅ 현재 상영작에는 OTT 정보 없음
                      />
                    </SwiperSlide>
                ))}
              </Swiper>
          )}
        </div>
      </div>
  );
};

export default NowPlayingSection;
