import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';

import { ErrorBoundary } from './error/components/ErrorBoundary';
import { ErrorModal } from './error/components/ErrorModal';
import { closeModal } from './error/redux/errorSlice';

// 페이지 컴포넌트 import
import IndexPage from './index/IndexPage';            // 메인 페이지
import SearchPage from './search/SearchPage';         // 검색 결과 페이지
import ContentsPage from './contents/ContentsPage';   // 콘텐츠 전체 페이지

/*
 * App 컴포넌트
 * - 전체 라우팅과 전역 에러 핸들링 관리
 * - Redux 기반 에러 모달 처리 포함
 */
function App() {
    const dispatch = useDispatch();
    const modal = useSelector((state) => state.error.modal);

    // 에러 모달 닫기 핸들러
    const handleClose = () => dispatch(closeModal());

    return (
        <Router>
            <ErrorBoundary>
                {/* 전체 라우팅 정의 */}
                <Routes>
                    <Route path="/" element={<IndexPage />} />
                    <Route path="/search" element={<SearchPage />} />
                    <Route path="/contents" element={<ContentsPage />} />
                </Routes>

                {/* 전역 에러 모달 처리 */}
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
