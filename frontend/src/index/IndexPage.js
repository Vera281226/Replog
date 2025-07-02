  // src/index/IndexPage.js
  import React from 'react';
  import './css/IndexPage.css';

  // 공통 컴포넌트
  import Header from '../components/Header';
  import Footer from '../components/Footer';
  import FabButton from '../components/FabButton';

  // 콘텐츠 섹션 컴포넌트
  import HotReviewSlider from './components/HotReviewSlider';
  import NowPlayingSection from './components/NowPlayingSection';
  import ContentsNetflix from './components/ContentsNetflix';
  import ContentsDisney from './components/ContentsDisney';
  import UpcomingSection from './components/UpcomingSection';
  import TrailerSection from './components/TrailerSection';
  import UpcomingMixSection from './components/UpcomingMixSection';

  // 검색창 컴포넌트
  import SearchBar from '../search/components/SearchBar';

  /**
   * IndexPage 컴포넌트
   * - 메인 홈 화면을 구성하는 최상위 페이지
   * - TMDB 기반 콘텐츠 섹션 및 검색 UI 포함
   */
  const IndexPage = () => {
    return (
        <>
          <main>
            {/* 검색창 영역 (자동완성 + 최근검색어 포함) */}
            <SearchBar />

            {/* 현재 상영 중인 영화 섹션 */}
            <section className="now-playing-section">
              <div className="section-inner">
                <h2 className="section-title">현재 상영 중인 영화</h2>
                <NowPlayingSection />
              </div>
            </section>

            {/* 넷플릭스 인기 콘텐츠 */}
            <ContentsNetflix />

            {/* 디즈니+ 인기 콘텐츠 */}
            <ContentsDisney />

            {/* 개봉 예정 영화 */}
            <UpcomingSection />

            {/* 지금 뜨는 리뷰 섹션
            <section>
              <div className="section-inner">
                <h2 className="section-title">지금 뜨는 리뷰</h2>
                <HotReviewSlider />
              </div>
            </section> */}

            {/* 추천 예고편 */}
            <TrailerSection />

            {/* 콘텐츠 예정작 (넷플릭스 + 디즈니 믹스) */}
            <UpcomingMixSection />
          </main>
        </>
    );
  };

  export default IndexPage;
