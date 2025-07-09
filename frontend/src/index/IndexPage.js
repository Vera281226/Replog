// src/index/IndexPage.js

import React from 'react';
import './css/IndexPage.css'; // ✅ 인덱스 페이지 전용 스타일

/**
 * ✅ 공통 포함 요소는 MainLayout.js에서 자동 포함됨
 * - Header, Footer, FabButton은 여기서 중복 제거됨
 */

/* 콘텐츠 섹션 컴포넌트 */
import HotReviewSlider from './components/HotReviewSlider';
import NowPlayingSection from './components/NowPlayingSection';
import ContentsNetflix from './components/ContentsNetflix';
import ContentsDisney from './components/ContentsDisney';
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

  /**
   * ✅ Hero 배경 이미지 경로 설정
   * - process.env.PUBLIC_URL은 public 폴더 기준 경로
   * - 만약 환경변수가 undefined일 경우를 대비하여 기본값 '' 처리
   * - Webpack이 CSS에서 public 접근을 허용하지 않기 때문에 JSX에서 처리해야 함
   */
  const heroBgUrl = `${process.env.PUBLIC_URL || ''}/images/replog.jpg`;

  return (
      <>
        <main>

          {/**
           * 🔷 Hero 환영 영역
           * - replog.jpg 이미지를 배경으로 사용
           * - 환영 문구 + 사이트 소개 문구 출력
           * - 검색창 포함
           * - ✅ 이미지 경로: /public/images/replog.jpg
           * - ✅ CSS 대신 JSX에서 style로 처리 (Webpack 경로 문제 해결)
           */}
          <div
              className="hero-section"
              style={{ backgroundImage: `url(${heroBgUrl})` }}
          >
            <div className="hero-overlay">
              <h1 className="hero-title">환영합니다</h1>
              <p className="hero-subtitle">
                수백만 개의 영화, OTT 정보 제공과 함께<br />
                리뷰 커뮤니케이션을 즐겨보세요!
              </p>
              <SearchBar /> {/* 🔍 통합 검색창 */}
            </div>
          </div>

          {/**
           * 🎬 현재 상영 중인 영화 섹션
           * - 백엔드 API: /api/index/now-playing
           * - 섹션 제목은 내부 컴포넌트에서 출력
           */}
          <NowPlayingSection />

          {/**
           * 💬 지금 뜨는 리뷰 섹션
           * - 백엔드 API: /api/index/hot-reviews
           */}
          <section>
            <div className="section-inner">
              <h2 className="section-title">지금 뜨는 리뷰</h2>
              <HotReviewSlider />
            </div>
          </section>

          {/**
           * 🔥 넷플릭스 인기 콘텐츠
           * - 백엔드 API: /api/index/netflix
           */}
          <ContentsNetflix />

          {/**
           * 🐭 디즈니+ 인기 콘텐츠
           * - 백엔드 API: /api/index/disney
           */}
          <ContentsDisney />

          {/**
           * 🎞️ 추천 예고편 섹션
           * - 백엔드 API: /api/index/trailers
           */}
          <TrailerSection />

        </main>
      </>
  );
};

export default IndexPage;
