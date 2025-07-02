// src/components/common/ReportButton.js
import React, { useState } from 'react';
import ReportModal from './ReportModal';

const ReportButton = ({ 
  targetType, 
  targetId, 
  buttonText = "신고",
  buttonStyle = "small",
  isRequest = false // 신고 vs 요청 구분
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const getButtonStyle = () => {
    const baseStyle = {
      backgroundColor: 'transparent',
      border: 'none',
      cursor: 'pointer',
      fontSize: '12px',
      color: '#666',
      padding: '2px 6px',
      borderRadius: '4px',
      transition: 'all 0.2s'
    };

    const styles = {
      small: { ...baseStyle },
      icon: { 
        ...baseStyle, 
        fontSize: '14px',
        padding: '4px',
        color: '#999'
      },
      text: {
        ...baseStyle,
        textDecoration: 'underline',
        fontSize: '11px'
      }
    };

    return styles[buttonStyle] || styles.small;
  };

  const getButtonContent = () => {
    if (buttonStyle === 'icon') {
      return isRequest ? '📝' : '🚨';
    }
    return buttonText;
  };

  return (
    <>
      <button
        onClick={(e) => {
          e.stopPropagation(); // 이벤트 버블링 방지
          setIsModalOpen(true);
        }}
        style={getButtonStyle()}
        title={isRequest ? '요청하기' : '신고하기'}
      >
        {getButtonContent()}
      </button>

      {isModalOpen && (
        <ReportModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          targetType={targetType}
          targetId={targetId}
          isRequest={isRequest}
        />
      )}
    </>
  );
};

export default ReportButton;
