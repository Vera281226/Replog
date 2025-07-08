// src/pages/ReviewPage.jsx
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useSelector } from 'react-redux';
import axios from '../error/api/interceptor';
import './ReviewPage.css';

import ContentDetail from '../components/ContentDetail';
import ReviewList from '../components/Review/ReviewList';
import ReviewModal from '../components/Review/ReviewModal';

import { selectCurrentUser } from '../error/redux/authSlice';

export default function ReviewPage() {
  const { contentId } = useParams();
  const contentIdNum = Number(contentId);

  const currentUser = useSelector(selectCurrentUser);
  const memberId = currentUser?.memberId;

  const [content, setContent] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [isModalOpen, setModalOpen] = useState(false);
  const [reviewCallback, setReviewCallback] = useState(null);

  // 콘텐츠 상세 조회
  useEffect(() => {
    async function fetchDetail() {
      try {
        setLoading(true);
        const res = await axios.get(`/contents/${contentIdNum}`);
        setContent(res.data);
      } catch (e) {
        console.error(e);
        setError('콘텐츠 로드 실패');
      } finally {
        setLoading(false);
      }
    }

    if (!isNaN(contentIdNum)) {
      fetchDetail();
    } else {
      setError('잘못된 콘텐츠 ID입니다');
      setLoading(false);
    }
  }, [contentIdNum]);

  // 모달 열기: 콜백 저장
  const openModal = (onReviewCreated) => {
    setReviewCallback(() => onReviewCreated);
    setModalOpen(true);
  };

  const closeModal = () => setModalOpen(false);

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
              posterUrl={
                content.posterPath
                  ? `https://image.tmdb.org/t/p/w500${content.posterPath}`
                  : '/assets/default-poster.png'
              }
              contentId={contentIdNum}
            />

            <ReviewList
              contentId={contentIdNum}
              memberId={memberId}
              openModal={openModal}
            />
          </>
        )}
      </main>

      {isModalOpen && (
        <ReviewModal
          contentId={contentIdNum}
          memberId={memberId}
          onClose={closeModal}
          onReviewCreated={() => {
            if (reviewCallback) reviewCallback(); // 안전하게 실행
            closeModal();
          }}
        />
      )}
    </>
  );
}
