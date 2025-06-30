import React from 'react';
import ReviewModal from './ReviewModal';

function ReviewPopup() {
  const handleComplete = () => {
    window.opener?.postMessage('reviewCreated', '*'); // 부모창에게 알림
    window.close(); // 팝업 닫기
  };

  return (
    <div className="p-4">
      <ReviewModal
        onClose={() => window.close()}           // 취소 누르면 닫힘
        onReviewCreated={handleComplete}         // 등록 성공 시 부모창 알림 + 닫기
        contentId={1}
        memberId="testUser1"
      />
    </div>
  );
}

export default ReviewPopup;
