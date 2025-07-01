// src/components/FabButton.js

import React, { useEffect } from 'react';
import './fab.css';

const FabButton = () => {
  // ✅ 다크/라이트 모드 자동 적용
  const applyTimeBasedMode = () => {
    const hour = new Date().getHours();
    if (hour >= 6 && hour < 18) {
      document.body.classList.add('light-mode');
      document.body.classList.remove('dark-mode');
    } else {
      document.body.classList.add('dark-mode');
      document.body.classList.remove('light-mode');
    }
  };

  // ✅ 버튼 클릭 시 수동 토글
  const toggleMode = () => {
    document.body.classList.toggle('dark-mode');
    document.body.classList.toggle('light-mode');
  };

  // ✅ 최초 렌더링 시 자동 적용
  useEffect(() => {
    applyTimeBasedMode();
  }, []);

  return (
    <div className="fab-container">
      <button className="fab settings" onClick={toggleMode}>⚙</button>
      <button className="fab">💬</button>
    </div>
  );
};

export default FabButton;
