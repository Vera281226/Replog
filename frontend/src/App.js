import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { ErrorBoundary } from './error/components/ErrorBoundary';
import { ErrorModal } from './error/components/ErrorModal';
import { closeModal } from './error/redux/errorSlice';

import ContentDetail from './components/ContentDetail';
import ReviewList from './components/Review/ReviewList';
import ReviewPopup from './components/Review/ReviewPopup'; 
import ReviewModal from './components/Review/ReviewModal';
import ReviewEditPopup from './components/Review/ReviewEditPopup';

function App() {
  const dispatch = useDispatch();
  const modal = useSelector((state) => state.error.modal);

  const handleClose = () => dispatch(closeModal());

  return (
    <Router>
      <ErrorBoundary>
        <Routes>
          {/* 메인 페이지 */}
          <Route
            path="/"
            element={
              <div className="max-w-4xl mx-auto mt-8">
                <ContentDetail />
                <ReviewList contentId={1} memberId="testUser1" />
              </div>
            }
          />

          {/* 팝업 페이지 */}
          <Route path="/review-popup" element={<ReviewPopup />} />
          <Route path="/review-edit-popup" element={<ReviewEditPopup />} />
        </Routes>

        {/* 에러 모달은 라우트 외부에 위치 */}
        {modal.isOpen && (
          <ErrorModal
            isOpen={modal.isOpen}
            title={modal.title}
            message={modal.message}
            onConfirm={() => {
              modal.onConfirm?.();
              handleClose();
            }}
            onCancel={() => {
              modal.onCancel?.();
              handleClose();
            }}
          />
        )}
      </ErrorBoundary>
    </Router>
  );
}

export default App;
