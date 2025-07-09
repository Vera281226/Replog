// src/components/Review/ReviewModal.jsx
import React, { useState, useEffect } from 'react';
import './ReviewModal.css';
import api from '../../error/api/interceptor';
import StarSelector from './StarSelector';

export default function ReviewModal({
  contentId,
  memberId,
  onClose,
  onReviewCreated,
  isEdit = false,
  initialData = null
}) {
  const [rating, setRating] = useState(initialData?.rating || 0);
  const [cont, setCont] = useState(initialData?.cont || '');
  const [isSpoiler, setIsSpoiler] = useState(initialData?.isSpoiler || false);

  // 모달 오픈 시 스크롤 잠금
  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = '';
    };
  }, []);

  const handleSubmit = async () => {
    if (rating < 1 || rating > 5) return alert('별점은 1~5 사이입니다.');
    if (!cont.trim()) return alert('내용을 입력하세요.');

    try {
      if (isEdit && initialData?.reviewId) {
        // 수정 요청
        await api.patch(`/reviews/${initialData.reviewId}`, {
          memberId,
          cont,
          rating,
          isSpoiler
        });
      } else {
        // 등록 요청
        await api.post('/reviews', {
          contentId,
          memberId,
          rating,
          cont,
          isSpoiler
        });
      }

      // 리뷰 등록/수정 후 평점 갱신을 위한 함수 호출
      onReviewCreated(); // 평점 갱신을 위한 함수 호출
    } catch (e) {
      alert(isEdit ? '수정 실패' : '등록 실패');
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={e => e.stopPropagation()} tabIndex={-1}>
        <h2 className="modal-title">{isEdit ? '리뷰 수정' : '리뷰 쓰기'}</h2>
        <textarea
          value={cont}
          onChange={e => setCont(e.target.value)}
          placeholder="리뷰 내용을 입력하세요"
        />
        <div className="rating-row">
          별점: <StarSelector rating={rating} onChange={setRating} />
        </div>
        <label className="spoiler-row">
          <input
            type="checkbox"
            checked={isSpoiler}
            onChange={e => setIsSpoiler(e.target.checked)}
          />
          스포일러 포함
        </label>
        <div className="btn-row">
          <button className="btn-cancel" onClick={onClose}>취소</button>
          <button className="btn-submit" onClick={handleSubmit}>
            {isEdit ? '수정' : '등록'}
          </button>
        </div>
      </div>
    </div>
  );
}
