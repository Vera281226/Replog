// src/index/components/HotReviewSlider.js

import React, { useEffect, useState } from 'react';
import '../css/HotReviewSlider.css';

const HotReviewSlider = () => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('/api/index/hot-reviews')
      .then(res => {
        if (!res.ok) {
          throw new Error('리뷰 데이터를 불러오지 못했습니다.');
        }
        return res.json();
      })
      .then(data => {
        setReviews(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) return <div className="review-slider">로딩 중...</div>;
  if (error) return <div className="review-slider">❌ {error}</div>;

  return (
    <div className="review-slider">
      {reviews.map((review) => (
        <div className="review-card" key={review.reviewId}>
          <span className="nickname">{review.nickname}</span>
          {review.cont}
          <div className="meta">
            🎬 {review.contentTitle} | ⭐ {review.rating}점
          </div>
        </div>
      ))}
    </div>
  );
};

export default HotReviewSlider;
