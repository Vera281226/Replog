// src/layouts/MainLayout.js
import React, { useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { selectCurrentUser, selectAuthLoading, fetchCurrentUser, logout } from '../error/redux/authSlice';

import Header from '../components/Header';
import Footer from '../components/Footer';
import ChatToggle from '../components/chat/ChatToggle';

export default function MainLayout() {
  const dispatch = useDispatch();
  const currentUser = useSelector(selectCurrentUser);
  const loading = useSelector(selectAuthLoading);

  useEffect(() => {
    dispatch(fetchCurrentUser());
  }, [dispatch]);

  const handleLogout = () => {
    dispatch(logout());
  };

  if (loading) {
    // 인증 체크 중에는 아무것도 렌더링하지 않음 (또는 로딩 스피너)
    return null;
    // return <div className="global-loading">로딩 중...</div>;
  }

  return (
    <>
      <Header currentUser={currentUser} onLogout={handleLogout} />
      <main style={{ minHeight: 'calc(100vh - 160px)' }}>
        <Outlet />
      </main>
      <ChatToggle currentUser={currentUser} />
      <Footer />
    </>
  );
}
