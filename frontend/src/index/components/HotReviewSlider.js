// src/index/components/HotReviewSlider.js

import React, { useEffect, useState, useMemo } from 'react';
import ReviewCard from './ReviewCard';
import '../css/HotReviewSlider.css';

const HotReviewSlider = () => {
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentSlide, setCurrentSlide] = useState(0);

    // ✅ 데이터 fetch
    useEffect(() => {
        fetch('/api/index/hot-reviews')
            .then((res) => {
                if (!res.ok) throw new Error('리뷰 데이터를 불러오지 못했습니다.');
                return res.json();
            })
            .then((data) => {
                setReviews(data.filter(Boolean));
                setLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setLoading(false);
            });
    }, []);

    // ✅ 3개씩 묶기
    const groupedReviews = useMemo(() => {
        const result = [];
        for (let i = 0; i < reviews.length; i += 3) {
            result.push(reviews.slice(i, i + 3));
        }
        return result;
    }, [reviews]);

    // ✅ 자동 슬라이드 (4초 간격)
    useEffect(() => {
        if (groupedReviews.length === 0) return;
        const interval = setInterval(() => {
            setCurrentSlide((prev) => (prev + 1) % groupedReviews.length);
        }, 4000);
        return () => clearInterval(interval);
    }, [groupedReviews]);

    // ✅ 렌더링
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
