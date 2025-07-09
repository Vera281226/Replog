// src/components/Header.js
import React, { useEffect, useState } from 'react';
import { Link, useNavigate  } from 'react-router-dom';
import './header.css';
import ReportButton from './common/ReportButton';
import axios from 'axios';

const Header = ({ currentUser, onLogout }) => {
  const isLoggedIn = !!currentUser?.memberId;
  const [isAdmin, setIsAdmin] = useState(false);
  const navigate = useNavigate();

 const handleLogout = async () => {
    await onLogout?.();
    navigate('/'); // index로 이동
  };

  useEffect(() => {
    const tooltipText = document.getElementById('tooltip-text');
    if (tooltipText) {
      tooltipText.textContent = isLoggedIn
        ? '찾으시는 영화나 TV 프로그램이 없나요?\n관리자에게 요청하세요.'
        : '찾으시는 영화나 TV 프로그램이 없나요?\n로그인 하셔서 직접 만들어주세요.';
    }
  }, [isLoggedIn]);

  useEffect(() => {
    if (!isLoggedIn) {
      setIsAdmin(false);
      return;
    }
    // 서버에 직접 요청해서 관리자 여부 확인
    const checkAdmin = async () => {
      try {
        await axios.get('/api/auth/admin-only', { withCredentials: true });
        setIsAdmin(true);
      } catch {
        setIsAdmin(false);
      }
    };
    checkAdmin();
  }, [isLoggedIn]);

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
          <ReportButton
            targetType="CONTENT_REQUEST"
            targetId="general"
            buttonStyle="icon"
            isRequest={true}
          />
          {isLoggedIn ? (
            <>
              {/* 관리자인 경우에만 "관리자" 메뉴 노출 */}
              {isAdmin && (
                <Link to="/admin" className="admin-menu" title="관리자 페이지">신고처리</Link>
              )}
              <Link to="/mypage" className="header-icon" title="마이페이지">마이페이지</Link>
              <button onClick={handleLogout} className="logout-btn">로그아웃</button>
            </>
          ) : (
            <>
              {/* 비로그인 시에는 관리자 메뉴 노출 X */}
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
