// src/components/Review/ReviewModal.jsx
import React, { useState, useEffect } from 'react';
import './ReviewModal.css';  // 추가
import api from '../../error/api/interceptor';
import StarSelector from './StarSelector';

export default function ReviewModal({ contentId, memberId, onClose, onReviewCreated }) {
  const [rating, setRating] = useState(0);
  const [cont, setCont] = useState('');
  const [isSpoiler, setIsSpoiler] = useState(false);

  // 모달 오픈 시 스크롤 잠금
  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = ''; };
  }, []);

  const handleSubmit = async () => {
    if (rating < 1 || rating > 5) return alert('별점은 1~5 사이입니다.');
    if (!cont.trim()) return alert('내용을 입력하세요.');

    try {
      await api.post('/reviews', { contentId, memberId, rating, cont, isSpoiler });
      onReviewCreated();
    } catch {
      alert('등록 실패');
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={e => e.stopPropagation()} tabIndex={-1}>
        <h2 className="modal-title">리뷰 쓰기</h2>
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
          <button className="btn-submit" onClick={handleSubmit}>등록</button>
        </div>
      </div>
    </div>
  );
}
