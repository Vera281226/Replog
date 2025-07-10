// src/index/components/HotReviewSlider.js
import React, { useEffect, useState, useMemo } from 'react';
import ReviewCard from './ReviewCard';
import api from '../../error/api/interceptor';  // 수정된 axios 인스턴스 경로
import '../css/HotReviewSlider.css';

const HotReviewSlider = () => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentSlide, setCurrentSlide] = useState(0);

  useEffect(() => {
    api.get('/index/hot-reviews')
      .then(res => {
        setReviews(res.data.filter(Boolean));
        setLoading(false);
      })
      .catch(err => {
        setError('리뷰 데이터를 불러오지 못했습니다.');
        setLoading(false);
      });
  }, []);

  const groupedReviews = useMemo(() => {
    const result = [];
    for (let i = 0; i < reviews.length; i += 3) {
      result.push(reviews.slice(i, i + 3));
    }
    return result;
  }, [reviews]);

  useEffect(() => {
    if (!groupedReviews.length) return;
    const interval = setInterval(() => {
      setCurrentSlide(prev => (prev + 1) % groupedReviews.length);
    }, 4000);
    return () => clearInterval(interval);
  }, [groupedReviews]);

  if (loading) return <div className="review-slider">로딩 중...</div>;
  if (error) return <div className="review-slider">❌ {error}</div>;

  return (
    <div className="review-slider">
      <div
        className="slider-inner hot-review-slider-inner"
        style={{ transform: `translateY(-${currentSlide * 280}px)` }}
      >
        {groupedReviews.map((group, index) => (
          <div className="review-group" key={index}>
            {group.map((review, idx) => (
              <ReviewCard
                key={review.reviewId ?? `${index}-${idx}`}
                review={review}
              />
            ))}
          </div>
        ))}
      </div>
    </div>
  );
};

export default HotReviewSlider;