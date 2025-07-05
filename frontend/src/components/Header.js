// src/components/Header.js
import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import './header.css';
import ReportButton from './common/ReportButton';

const Header = ({ currentUser, onLogout }) => {
  const isLoggedIn = !!currentUser?.memberId;

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
        <Link to="/"><strong>Replog</strong></Link>
      </div>

      <nav>
        <div className="left-menu">
          <Link to="/contents">콘텐츠</Link>
          <Link to="/boards">게시판</Link>
          <Link to="/theaters">시네마 메이트</Link>
        </div>

        <div className="right-menu">
          {/* ✅ 콘텐츠 요청 버튼 (＋) */}
         <ReportButton
          targetType="CONTENT_REQUEST"
           targetId="general"
          buttonStyle="icon"
            isRequest={true}
         />

          {isLoggedIn ? (
            <>
              <Link to="/mypage" className="header-icon" title="마이페이지">👤</Link>
              <button 
                onClick={onLogout} 
                className="logout-btn"
              >
                로그아웃
              </button>
            </>
          ) : (
            <>
              <Link to="/login">로그인</Link>
              <Link to="/signup">회원가입</Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
};

export default Header;
