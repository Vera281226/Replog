import React, { useEffect, useState } from 'react';
import axios from 'axios';
import MovieCard from './MovieCard'; // ✅ 공통 카드 컴포넌트

/**
 * ✅ UpcomingMixSection 컴포넌트
 * - 넷플릭스/디즈니+ 콘텐츠를 백엔드 API에서 받아와 카드로 출력
 * - TMDB API는 백엔드에서만 호출하며, 프론트는 직접 호출하지 않음
 */
const UpcomingMixSection = () => {
  const [contents, setContents] = useState([]);

  useEffect(() => {
    const fetchUpcoming = async () => {
      try {
        const res = await axios.get('/api/index/upcoming-mix');
        setContents(res.data);
      } catch (err) {
        console.error('❌ 콘텐츠 예정작 불러오기 실패:', err);
      }
    };

    fetchUpcoming();
  }, []);

  return (
      <section className="section upcoming-mix">
        <div className="section-inner">
          <h2 className="section-title">콘텐츠 예정작</h2>
          <div className="card-slider">
            {contents.map((content, index) => (
                <MovieCard
                    key={content.contentId}
                    title={`${index + 1}. ${content.title}`}
                    posterPath={content.posterPath}
                    releaseDate={content.releaseDate}
                    platform={content.platform} // ✅ 로고 출력용
                    contentId={content.contentId}
                />
            ))}
          </div>
        </div>
      </section>
  );
};

export default UpcomingMixSection;
