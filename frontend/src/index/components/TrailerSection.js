// src/index/components/TrailerSection.jsx

import React, { useEffect, useState } from 'react';
import axios from '../../error/api/interceptor';
import TrailerCard from './TrailerCard'; // ✅ 예고편 카드 컴포넌트

/**
 * TrailerSection 컴포넌트
 * -------------------------------------------------------------------
 * ○ 추천 예고편 섹션
 * ○ 백엔드 API: GET /api/index/trailers
 * ○ 최대 6개의 예고편 카드(TrailerCard) 출력
 * -------------------------------------------------------------------
 */
const TrailerSection = () => {
  const [trailers, setTrailers] = useState([]);
  const [error, setError] = useState(null);

  // ✅ 예고편 호출 (백엔드 API 사용)
  useEffect(() => {
    const fetchTrailers = async () => {
      try {
        const response = await axios.get('/index/trailers');
        setTrailers(response.data);
      } catch (err) {
        console.error('❌ 예고편 호출 실패:', err);
        setError('추천 예고편을 불러올 수 없습니다.');
      }
    };

    fetchTrailers();
  }, []);

  return (
      <section>
        <div className="section-inner">
          <h2 className="section-title">추천 예고편</h2>

          {/* 오류 출력 */}
          {error && <p className="error-message">{error}</p>}

          {/* ✅ 예고편 카드 출력 */}
          <div className="card-grid">
            {trailers.map((item) => (
                <TrailerCard
                    key={item.contentId}
                    title={item.title}
                    description={item.overview}
                    videoKey={item.youtubeKey}
                />
            ))}
          </div>
        </div>
      </section>
  );
};

export default TrailerSection;
