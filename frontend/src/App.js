import React from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { ErrorBoundary } from './error/components/ErrorBoundary';
import { ErrorModal } from './error/components/ErrorModal';
import { closeModal } from './error/redux/errorSlice';

function App() {
  const dispatch = useDispatch();
  const modal = useSelector(state => state.error.modal);
  
  const handleClose = () => dispatch(closeModal());

  return (
    <Router>
      <ErrorBoundary>
        <div>
          <h1>Hello React!</h1>
          {/* 여기에 앱 내용 추가 */}
        </div>
        
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