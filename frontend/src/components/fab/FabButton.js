// src/components/common/FabButton.js

import React, { useState } from 'react';
import './FabButton.css';
import SettingsPopover from './SettingsPopover';

const FabButton = () => {
  const [showPopover, setShowPopover] = useState(false);

  // ✅ 팝오버 열기/닫기
  const togglePopover = () => {
    setShowPopover((prev) => !prev);
  };

  return (
      <>
        <button className="fab-settings" onClick={togglePopover}>
          ⚙
        </button>

        {/* ✅ 설정 팝오버 표시 */}
        {showPopover && <SettingsPopover onClose={() => setShowPopover(false)} />}
      </>
  );
};

export default FabButton;
