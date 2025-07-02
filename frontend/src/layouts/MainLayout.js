// src/layouts/MainLayout.js
import React, { useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { selectCurrentUser, fetchCurrentUser, logout } from '../error/redux/authSlice';

import Header from '../components/Header';
import Footer from '../components/Footer';
import ChatToggle from '../components/chat/ChatToggle';

export default function MainLayout() {
  const dispatch = useDispatch();
  const currentUser = useSelector(selectCurrentUser);

  // 앱 시작 시 세션 확인하여 Redux에 저장
  useEffect(() => {
    dispatch(fetchCurrentUser());
  }, [dispatch]);

  // 로그아웃 핸들러
  const handleLogout = () => {
    dispatch(logout());
  };

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
