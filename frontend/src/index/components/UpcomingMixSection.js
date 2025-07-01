// src/index/components/UpcomingMixSection.js

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import MovieCard from './MovieCard'; // ✅ 공통 카드 컴포넌트

// ✅ 실제 TMDB API 키
const API_KEY = '16a0ea8699fa78781abae853600c2b61';

const UpcomingMixSection = () => {
  const [contents, setContents] = useState([]);

  // ✅ 넷플릭스 + 디즈니+ 예정작 10개씩 가져와 랜덤 6개 추출
  useEffect(() => {
    const fetchUpcoming = async () => {
      try {
        // ✅ 넷플릭스 예정작 (provider: 8)
        const netflixRes = await axios.get(
          `https://api.themoviedb.org/3/discover/movie?with_watch_providers=8&watch_region=KR&language=ko-KR&sort_by=release_date.asc&include_adult=false&include_video=false&page=1&api_key=${API_KEY}`
        );

        // ✅ 디즈니+ 예정작 (provider: 337)
        const disneyRes = await axios.get(
          `https://api.themoviedb.org/3/discover/movie?with_watch_providers=337&watch_region=KR&language=ko-KR&sort_by=release_date.asc&include_adult=false&include_video=false&page=1&api_key=${API_KEY}`
        );

        // ✅ 각 플랫폼별 10개 → platform 속성 추가
        const netflixList = netflixRes.data.results
          .slice(0, 10)
          .map(item => ({ ...item, platform: 'netflix' }));

        const disneyList = disneyRes.data.results
          .slice(0, 10)
          .map(item => ({ ...item, platform: 'disney' }));

        // ✅ 합치고 섞은 뒤 6개만 선택
        const mixed = [...netflixList, ...disneyList];
        const shuffled = mixed.sort(() => 0.5 - Math.random()).slice(0, 6);

        setContents(shuffled);
      } catch (err) {
        console.error('❌ 콘텐츠 예정작 불러오기 실패:', err);
      }
    };

    fetchUpcoming();
  }, []);

  return (
    <section>
      <div className="section-inner">
        <h2 className="section-title">콘텐츠 예정작</h2>
        <div className="card-slider">
          {contents.map((content, index) => (
            <MovieCard
              key={content.id}
              title={`${index + 1}. ${content.title}`}
              posterPath={content.poster_path}
              releaseDate={content.release_date}
              voteAverage={content.vote_average}
              voteCount={content.vote_count}
              platform={content.platform} // ✅ 로고 출력을 위한 platform 정보 전달
            />
          ))}
        </div>
      </div>
    </section>
  );
};

export default UpcomingMixSection;
