// src/components/Header.js

import React, { useEffect } from 'react';
import { Link } from 'react-router-dom'; // ✅ SPA 라우팅을 위한 Link 컴포넌트
import './header.css';

const Header = () => {
  // ✅ 로그인 여부 (추후 전역 상태 또는 props로 대체 가능)
  const isLoggedIn = false;

  // ✅ 로그인 상태에 따른 툴팁 문구 설정
  useEffect(() => {
    const tooltipText = document.getElementById('tooltip-text');
    if (tooltipText) {
      tooltipText.textContent = isLoggedIn
          ? '찾으시는 영화나 TV 프로그램이 없나요?\n관리자에게 요청하세요.'
          : '찾으시는 영화나 TV 프로그램이 없나요?\n로그인 하셔서 직접 만들어주세요.';
    }
  }, [isLoggedIn]);

  return (
      <header>
        <div className="logo">
          {/* ✅ 메인 홈 이동 */}
          <Link to="/"><strong>로고</strong></Link>
        </div>

        <nav>
          <div className="left-menu">
            {/* ✅ 각 메뉴 라우팅 처리 */}
            <Link to="/contents">콘텐츠</Link>
            <Link to="/board">게시판</Link>
            <Link to="/mate">시네마 메이트</Link>
          </div>

          <div className="right-menu">
            {/* ✅ 툴팁 버튼 (+) */}
            <div className="add-tooltip">
              <span>＋</span>
              <div className="tooltip-text" id="tooltip-text"></div>
            </div>

            {/* ✅ 로그인/회원가입 라우팅 */}
            <Link to="/login">로그인</Link>
            <Link to="/signup">회원가입</Link>
          </div>
        </nav>
      </header>
  );
};

export default Header;
