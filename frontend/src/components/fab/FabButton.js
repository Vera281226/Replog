// src/components/common/FabButton.js

import React, { useEffect, useState } from 'react';
import './FabButton.css';
import SettingsPopover from './SettingsPopover';

const FabButton = () => {
  const [showPopover, setShowPopover] = useState(false);

  // ✅ 저장된 테마 불러오기 또는 시간 기반 기본 적용
  useEffect(() => {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
      document.body.classList.add('dark-mode');
    } else if (savedTheme === 'light') {
      document.body.classList.add('light-mode');
    } else {
      const hour = new Date().getHours();
      const isDay = hour >= 6 && hour < 18;
      document.body.classList.add(isDay ? 'light-mode' : 'dark-mode');
    }

    const savedFontSize = localStorage.getItem('fontSize');
    if (savedFontSize) {
      document.body.classList.add(`font-${savedFontSize}`);
    }
  }, []);

  // ✅ 팝오버 토글
  const togglePopover = () => {
    setShowPopover(!showPopover);
  };

  return (
      <>
        <button className="fab-settings" onClick={togglePopover}>
          ⚙
        </button>
        {showPopover && <SettingsPopover onClose={() => setShowPopover(false)} />}
      </>
  );
};

export default FabButton;
