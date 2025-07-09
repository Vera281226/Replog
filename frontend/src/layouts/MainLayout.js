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
import FabButton from '../components/fab/FabButton'; // ✅ FAB 설정 버튼 추가
import '../components/fab/theme/fontSize.css';


export default function MainLayout() {
    const dispatch = useDispatch();
    const currentUser = useSelector(selectCurrentUser);
    const loading = useSelector(selectAuthLoading);

    useEffect(() => {
        dispatch(fetchCurrentUser());

        // ✅ 글자 크기 초기 설정 (html 태그에 class 추가)
        const savedFont = localStorage.getItem('fontSize') || 'normal';
        const html = document.documentElement;
        html.classList.remove('font-small', 'font-normal', 'font-large');
        html.classList.add(`font-${savedFont}`);
    }, [dispatch]);

    const handleLogout = () => {
        dispatch(logout());
    };

    if (loading) return null; // 또는 로딩 스피너

    return (
        <>
            <Header currentUser={currentUser} onLogout={handleLogout} />

            <main style={{ minHeight: 'calc(100vh - 160px)' }}>
                <Outlet />
            </main>

            <ChatToggle currentUser={currentUser} />

            <FabButton /> {/* ✅ 설정 버튼은 항상 우측 하단에 고정 */}

            <Footer />
        </>
    );
}
