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
    // 요청 버튼만 "요청하기" 글자로, 나머지는 기존대로
    if (isRequest) {
      return "요청하기";
    }
    if (buttonStyle === 'icon') {
      return '🚨';
    }
    return buttonText;
  };

  return (
    <>
      {isRequest ? (
        // 요청 버튼은 span으로, 글자형태로 출력
        <span
  className="report-btn text"
  onClick={(e) => {
    e.stopPropagation();
    setIsModalOpen(true);
  }}
  title="요청하기"
>
  요청하기
</span>
      ) : (
        // 신고 버튼은 기존대로
        <button
          onClick={(e) => {
            e.stopPropagation();
            setIsModalOpen(true);
          }}
          style={getButtonStyle()}
          title="신고하기"
        >
          {getButtonContent()}
        </button>
      )}

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
