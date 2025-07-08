// src/index/IndexPage.js

import React from 'react';
import './css/IndexPage.css';

/**
 * ✅ 공통 포함 요소는 MainLayout.js에서 자동 포함됨
 * - Header, Footer, FabButton은 여기서 중복 제거됨
 */

/* 콘텐츠 섹션 컴포넌트 */
import HotReviewSlider from './components/HotReviewSlider';
import NowPlayingSection from './components/NowPlayingSection';
import ContentsNetflix from './components/ContentsNetflix';
import ContentsDisney from './components/ContentsDisney';
import UpcomingSection from './components/UpcomingSection';
import TrailerSection from './components/TrailerSection';

/* 검색창 컴포넌트 */
import SearchBar from '../search/components/SearchBar';

/**
 * IndexPage 컴포넌트
 * - 메인 홈 화면을 구성하는 최상위 페이지
 * - TMDB 기반 콘텐츠 섹션 및 검색 UI 포함
 * - 공통 레이아웃(Header, Footer, FAB)은 MainLayout에서 관리
 */
const IndexPage = () => {
  return (
      <>
        <main>
          {/**
           * 🔍 검색창 영역
           * - 자동완성, 최근 검색어 포함 (컴포넌트 내부에서 제어)
           */}
          <SearchBar />

          {/**
           * 🎬 현재 상영 중인 영화
           * - 백엔드 API: /api/index/now-playing
           * - 제목은 NowPlayingSection 내부에서 출력
           */}
          <NowPlayingSection />

          {/**
           * 💬 지금 뜨는 리뷰 섹션
           * - API: /api/index/hot-reviews
           */}
          <section>
            <div className="section-inner">
              <h2 className="section-title">지금 뜨는 리뷰</h2>
              <HotReviewSlider />
            </div>
          </section>

          {/**
           * 🔥 넷플릭스 인기 콘텐츠
           */}
          <ContentsNetflix />

          {/**
           * 🐭 디즈니+ 인기 콘텐츠
           */}
          <ContentsDisney />

          {/**
           * 🎞️ 추천 예고편
           */}
          <TrailerSection />

          {/**
           * 📅 개봉 예정 영화
           */}
          <UpcomingSection />
        </main>
      </>
  );
};

export default IndexPage;
