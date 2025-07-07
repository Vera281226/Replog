// src/components/Review/ReviewEditModal.jsx
import React, { useEffect, useState } from 'react';
import './ReviewModal.css';
import api from '../../error/api/interceptor';
import StarSelector from './StarSelector';

export default function ReviewEditModal({
  reviewId,
  memberId,
  initialCont,
  initialRating,
  initialSpoiler,
  onClose,
  onUpdated
}) {
  const [cont, setCont] = useState(initialCont || '');
  const [rating, setRating] = useState(initialRating || 1);
  const [isSpoiler, setIsSpoiler] = useState(initialSpoiler || false);

  useEffect(() => {
    document.body.style.overflow = 'hidden';
    return () => { document.body.style.overflow = ''; };
  }, []);

  const handleUpdate = async () => {
    if (rating < 1 || rating > 5) return alert('별점은 1~5 사이입니다.');
    if (!cont.trim()) return alert('내용을 입력하세요.');

    try {
      await api.patch(`/reviews/${reviewId}`, {
        memberId,
        cont,
        rating,
        isSpoiler
      });
      onUpdated(); // 수정 완료 후 상위에서 새로고침 등 처리
      onClose();
    } catch (err) {
      alert('수정 실패');
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={e => e.stopPropagation()} tabIndex={-1}>
        <h2 className="modal-title">리뷰 수정</h2>
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
          <button className="btn-submit" onClick={handleUpdate}>수정</button>
        </div>
      </div>
    </div>
  );
}
