// src/components/Review/ReviewList.jsx
import { useEffect, useState, useCallback } from 'react';
import api from '../../error/api/interceptor';
import ReviewItem from './ReviewItem';
import './ReviewList.css';

function ReviewList({ contentId, memberId, onCommentAdded, openModal }) {
  const [reviews, setReviews] = useState([]);
  const [sortType, setSortType] = useState('LATEST');

  const fetchReviews = useCallback(async () => {
    try {
      const res = await api.get('/reviews', {
        params: { contentId: contentId || 1, memberId: memberId || 'testUser1', sortType },
      });
      setReviews(res.data);
      onCommentAdded?.();
    } catch (err) {
      console.error('리뷰 목록 불러오기 실패:', err);
    }
  }, [contentId, memberId, sortType, onCommentAdded]);

  useEffect(() => { fetchReviews(); }, [fetchReviews]);

  return (
    <div className="review-list">
      <div className="review-list-header">
        <h2>리뷰 목록</h2>
        <div className="review-controls">
          <button className="btn-create-review" onClick={openModal}>리뷰 작성</button>
          <select value={sortType} onChange={(e) => setSortType(e.target.value)} className="sort-dropdown">
            <option value="LATEST">최신순</option>
            <option value="RATING">별점 높은 순</option>
          </select>
        </div>
      </div>

      <div>
        {reviews
          .filter((r) => r.gnum === r.reviewId)
          .sort((a, b) => {
            if (a.memberId === memberId && b.memberId !== memberId) return -1;
            if (a.memberId !== memberId && b.memberId === memberId) return 1;
            return sortType === 'RATING'
              ? (b.rating || 0) - (a.rating || 0)
              : new Date(b.createdAt) - new Date(a.createdAt);
          })
          .map((review) => (
            <div key={review.reviewId}>
              <ReviewItem
                review={review}
                allReviews={reviews}
                onCommentAdded={fetchReviews}
                memberId={memberId}
              />
            </div>
          ))}
      </div>
    </div>
  );
}

export default ReviewList;
