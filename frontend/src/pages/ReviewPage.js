// src/pages/ReviewPage.jsx
import React, { useEffect, useState, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import axios from '../error/api/interceptor';
import './ReviewPage.css';
import Header from '../components/Header';
import Footer from '../components/Footer';
import ContentDetail from '../components/ContentDetail';
import ReviewList from '../components/Review/ReviewList';
import ReviewModal from '../components/Review/ReviewModal';
import { fetchCurrentUser, selectCurrentUser, logout } from '../error/redux/authSlice';

export default function ReviewPage() {
  const { contentId } = useParams();
  const dispatch = useDispatch();
  const currentUser = useSelector(selectCurrentUser);
  const memberId = currentUser?.memberId;

  const [content, setContent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isModalOpen, setModalOpen] = useState(false);

  // 콘텐츠 상세 조회
  useEffect(() => {
    async function fetchDetail() {
      try {
        setLoading(true);
        const res = await axios.get(`/contents/${contentId}`);
        setContent(res.data);
      } catch (e) {
        console.error(e);
        setError('콘텐츠 로드 실패');
      } finally {
        setLoading(false);
      }
    }
    fetchDetail();
  }, [contentId]);

  const openModal = () => setModalOpen(true);
  const closeModal = () => setModalOpen(false);
  const handleReviewCreated = useCallback(() => {
    closeModal();
    // ReviewList가 fetchReviews prop으로 새로고침하도록 onCommentAdded 호출
  }, []);

  return (
    <>

      <main style={{ maxWidth: 800, margin: '0 auto', padding: 20 }}>
        {loading && <p>로딩 중...</p>}
        {error && <p style={{ color: 'red' }}>{error}</p>}
        {content && (
          <>
            <ContentDetail
              title={content.title}
              releaseDate={content.releaseDate?.toString() || ''}
              genres={content.genres || []}
              cast={content.cast || []}
              rating={content.rating || 0}
              description={content.overview}
              posterUrl={content.posterPath
                ? `https://image.tmdb.org/t/p/w500${content.posterPath}`
                : '/assets/default-poster.png'}
            />

            


            {/* 리뷰 리스트 */}
            <ReviewList
              contentId={Number(contentId)}
              memberId={memberId}
              onCommentAdded={() => {
                // fetchReviews() 다시 호출하는 로직
              }}
              openModal={openModal} // ✅ 이거 추가
            />
          </>
        )}
      </main>

      {/* 모달 */}
      {isModalOpen && (
        <ReviewModal
          contentId={Number(contentId)}
          memberId={memberId}
          onClose={closeModal}
          onReviewCreated={handleReviewCreated}
        />
      )}
    </>
  );
}
