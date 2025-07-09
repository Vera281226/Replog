// src/layouts/MainLayout.js

import React, { useEffect } from 'react';
import { Outlet } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';

import {
    selectCurrentUser,
    selectAuthLoading,
    fetchCurrentUser,
    logout,
} from '../error/redux/authSlice';

import Header from '../components/Header';
import Footer from '../components/Footer';
import ChatToggle from '../components/chat/ChatToggle';
import FabButton from '../components/fab/FabButton';
import '../components/fab/theme/fontSize.css';

export default function MainLayout() {
    const dispatch = useDispatch();
    const currentUser = useSelector(selectCurrentUser);
    const loading = useSelector(selectAuthLoading);

    useEffect(() => {
        dispatch(fetchCurrentUser());

        const html = document.documentElement;

        // -------------------------------------
        // 🔠 글자 크기 초기 설정 (fontSize)
        // -------------------------------------
        const savedFont = localStorage.getItem('fontSize') || 'normal';
        html.classList.remove('font-small', 'font-normal', 'font-large');
        html.classList.add(`font-${savedFont}`);

        // -------------------------------------
        // 🌗 테마 초기 설정 (자동 + 수동 병행)
        // - 자동: 시간 기준 적용 (오전 6시~18시 라이트 / 나머지 다크)
        // - (오전 5시~오후 6시 다크모드) 예시 : const isNight = currentHour >= 5 && currentHour <= 18;
        // - const isNight = currentHour >= 18 || currentHour < 6; 시간 변경 코드
        // -------------------------------------
        const savedTheme = localStorage.getItem('theme'); // 'dark' | 'light' | null
        const currentHour = new Date().getHours();
        const isNight = currentHour >= 18 || currentHour < 6;
        const timeBasedTheme = isNight ? 'dark-mode' : 'light-mode';

        html.classList.remove('dark-mode', 'light-mode');

        // ✅ 수동 설정 있으면 우선 적용, 없으면 시간 기반 자동 적용
        if (savedTheme === 'dark') {
            html.classList.add('dark-mode');
        } else if (savedTheme === 'light') {
            html.classList.add('light-mode');
        } else {
            html.classList.add(timeBasedTheme);
        }
    }, [dispatch]);

    const handleLogout = () => {
        dispatch(logout());
    };

    if (loading) return null;

    return (
        <>
            <Header currentUser={currentUser} onLogout={handleLogout} />

            <main style={{ minHeight: 'calc(100vh - 160px)' }}>
                <Outlet />
            </main>

            <ChatToggle currentUser={currentUser} />
            <FabButton />

            <Footer />
        </>
    );
}
