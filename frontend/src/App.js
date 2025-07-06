import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';

import { ErrorBoundary } from './error/components/ErrorBoundary';
import { ErrorModal } from './error/components/ErrorModal';
import { closeModal } from './error/redux/errorSlice';

import MainLayout from './layouts/MainLayout';   // ⭐ 새로 만든 레이아웃
import IndexPage    from './index/IndexPage';
import SearchPage   from './search/SearchPage';
import ContentsPage from './contents/ContentsPage';
import BoardPage    from './pages/BoardPage';
import TheaterPage from './pages/TheaterPage';
import LoginForm from './components/member/LoginForm';
import MemberRegisterForm from './components/member/MemberRegisterForm';
import MyPageMain from './pages/mypage/MyPageMain';
import ProfileEdit from './pages/mypage/ProfileEdit';
import WritePostPage from './pages/WritePostPage';
import ReviewPage from './pages/ReviewPage';
import PostDetailPage from './pages/PostDetailPage';
import PartyPostDetailPage from './pages/PartyPostDetailPage';

export default function RootRouter() {
  const dispatch = useDispatch();
  const modal = useSelector((state) => state.error.modal);
  const handleClose = () => dispatch(closeModal());

  return (
    <Router>
      <ErrorBoundary>
        <Routes>
          {/* 모든 “정상 페이지”는 MainLayout 아래에 둔다 */}
          <Route element={<MainLayout />}>
            <Route path="/"          element={<IndexPage />} />
            <Route path="/search"    element={<SearchPage />} />
            <Route path="/contents"  element={<ContentsPage />} />
            <Route path="/boards"    element={<BoardPage />} />
            <Route path="/boards/:postNo" element={<PostDetailPage />} />
            <Route path="/boards/write"    element={<WritePostPage/>} />
            <Route path="/theaters"      element={<TheaterPage />} />
            <Route path="/theaters/:partyPostNo" element={<PartyPostDetailPage />} />
            <Route path="/login"      element={<LoginForm />} />
            <Route path="/signup"     element={<MemberRegisterForm/>}/>
            <Route path="/mypage"     element={<MyPageMain/>}/>
            <Route path="/mypage/edit"     element={<ProfileEdit/>}/>
            <Route path="/contents/:contentId/reviews" element={<ReviewPage />} />
          </Route>

          {/* 404 등 레이아웃이 필요 없는 라우트가 있으면 여기 추가 */}
          {/* <Route path="*" element={<NotFound />} /> */}
        </Routes>

        {/* 전역 에러 모달 */}
        {modal.isOpen && (
          <ErrorModal
            isOpen={modal.isOpen}
            title={modal.title}
            message={modal.message}
            onConfirm={() => { modal.onConfirm?.(); handleClose(); }}
            onCancel={()  => { modal.onCancel?.();  handleClose(); }}
          />
        )}
      </ErrorBoundary>
    </Router>
  );
}