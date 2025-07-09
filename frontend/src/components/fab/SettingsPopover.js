// src/components/common/SettingsPopover.js

import React, { useState, useEffect } from 'react';
import './SettingsPopover.css';

const SettingsPopover = ({ onClose }) => {
    // ----------------------------
    // 🌗 테마 상태: 'light', 'dark', null(자동)
    // ----------------------------
    const [theme, setTheme] = useState(null);

    // 🔠 글자 크기 상태: 'small' | 'normal' | 'large'
    const [fontSize, setFontSize] = useState('normal');

    // ----------------------------
    // ✅ 초기 설정 불러오기
    // ----------------------------
    useEffect(() => {
        const savedTheme = localStorage.getItem('theme');
        const savedFont = localStorage.getItem('fontSize');

        if (savedTheme === 'light' || savedTheme === 'dark') {
            setTheme(savedTheme);
        } else {
            setTheme(null); // 자동 모드
        }

        if (savedFont) setFontSize(savedFont);
    }, []);

    // ----------------------------
    // 🌗 테마 변경 핸들러
    // ----------------------------
    const handleThemeChange = (mode) => {
        setTheme(mode);
        localStorage.setItem('theme', mode); // 사용자 수동 선택

        const html = document.documentElement;
        html.classList.remove('light-mode', 'dark-mode');
        html.classList.add(`${mode}-mode`); // 'light-mode' or 'dark-mode'
    };

    // ⏱ 자동 모드: 시간 기준 적용 (오전 6시~18시 라이트 / 나머지 다크)
    const handleAutoTheme = () => {
        setTheme(null);
        localStorage.removeItem('theme');

        const currentHour = new Date().getHours();
        const isNight = currentHour >= 18 || currentHour < 6;
        const html = document.documentElement;

        html.classList.remove('light-mode', 'dark-mode');
        html.classList.add(isNight ? 'dark-mode' : 'light-mode');
    };

    // ----------------------------
    // 🔠 글자 크기 변경 핸들러
    // ----------------------------
    const handleFontChange = (size) => {
        setFontSize(size);
        localStorage.setItem('fontSize', size);

        const html = document.documentElement;
        html.classList.remove('font-small', 'font-normal', 'font-large');
        html.classList.add(`font-${size}`);
    };

    // ----------------------------
    // ✅ 렌더링
    // ----------------------------
    return (
        <div className="settings-popover">
            {/* 🌗 테마 선택 섹션 */}
            <div className="section">
                <div className="section-title">화면 스타일</div>
                <div className="option-list">
                    <button
                        className={theme === null ? 'active' : ''}
                        onClick={handleAutoTheme}
                    >
                        ⏱ 자동 모드 {theme === null && '✓'}
                    </button>
                    <button
                        className={theme === 'light' ? 'active' : ''}
                        onClick={() => handleThemeChange('light')}
                    >
                        ☀️ 라이트 모드 {theme === 'light' && '✓'}
                    </button>
                    <button
                        className={theme === 'dark' ? 'active' : ''}
                        onClick={() => handleThemeChange('dark')}
                    >
                        🌙 다크 모드 {theme === 'dark' && '✓'}
                    </button>
                </div>
            </div>

            {/* 🔠 글자 크기 선택 섹션 */}
            <div className="section">
                <div className="section-title">글자 크기</div>
                <div className="option-list">
                    <button
                        className={fontSize === 'small' ? 'active' : ''}
                        onClick={() => handleFontChange('small')}
                    >
                        축소 {fontSize === 'small' && '✓'}
                    </button>
                    <button
                        className={fontSize === 'normal' ? 'active' : ''}
                        onClick={() => handleFontChange('normal')}
                    >
                        기본 {fontSize === 'normal' && '✓'}
                    </button>
                    <button
                        className={fontSize === 'large' ? 'active' : ''}
                        onClick={() => handleFontChange('large')}
                    >
                        확대 {fontSize === 'large' && '✓'}
                    </button>
                </div>
            </div>

            {/* ❌ 닫기 버튼 */}
            <div className="close-area">
                <button onClick={onClose}>닫기</button>
            </div>
        </div>
    );
};

export default SettingsPopover;
