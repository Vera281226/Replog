// src/components/common/SettingsPopover.js

import React, { useState, useEffect } from 'react';
import './SettingsPopover.css';

const SettingsPopover = ({ onClose }) => {
    const [theme, setTheme] = useState('light');
    const [fontSize, setFontSize] = useState('normal');

    // ✅ 초기 설정 불러오기
    useEffect(() => {
        const savedTheme = localStorage.getItem('theme');
        const savedFont = localStorage.getItem('fontSize');

        if (savedTheme) setTheme(savedTheme);
        if (savedFont) setFontSize(savedFont);
    }, []);

    // ✅ 테마 설정 핸들러
    const handleThemeChange = (mode) => {
        setTheme(mode);
        localStorage.setItem('theme', mode);

        document.body.classList.remove('light-mode', 'dark-mode');
        document.body.classList.add(`${mode}-mode`);
    };

    // ✅ 글자 크기 설정 핸들러 (html 태그 기준으로 수정됨)
    const handleFontChange = (size) => {
        setFontSize(size);
        localStorage.setItem('fontSize', size);

        const html = document.documentElement;
        html.classList.remove('font-small', 'font-normal', 'font-large');
        html.classList.add(`font-${size}`);
    };

    return (
        <div className="settings-popover">
            <div className="section">
                <div className="section-title">화면 스타일</div>
                <div className="option-list">
                    <button
                        className={theme === 'light' ? 'active' : ''}
                        onClick={() => handleThemeChange('light')}
                    >
                        라이트 모드 {theme === 'light' && '✓'}
                    </button>
                    <button
                        className={theme === 'dark' ? 'active' : ''}
                        onClick={() => handleThemeChange('dark')}
                    >
                        다크 모드 {theme === 'dark' && '✓'}
                    </button>
                </div>
            </div>

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

            <div className="close-area">
                <button onClick={onClose}>닫기</button>
            </div>
        </div>
    );
};

export default SettingsPopover;
