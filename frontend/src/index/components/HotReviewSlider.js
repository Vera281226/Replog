// src/index/components/HotReviewSlider.js

import React, { useEffect, useState, useMemo } from 'react';
import ReviewCard from './ReviewCard';
import '../css/HotReviewSlider.css';

const HotReviewSlider = () => {
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [currentSlide, setCurrentSlide] = useState(0);

    // ✅ 4초마다 슬라이드 인덱스 증가
    useEffect(() => {
        if (reviews.length === 0) return;

        const interval = setInterval(() => {
            setCurrentSlide((prev) => (prev + 1) % Math.ceil(reviews.length / 3));
        }, 4000);

        return () => clearInterval(interval);
    }, [reviews]);

    // ✅ 데이터 fetch
    useEffect(() => {
        fetch('/api/index/hot-reviews')
            .then((res) => {
                if (!res.ok) throw new Error('리뷰 데이터를 불러오지 못했습니다.');
                return res.json();
            })
            .then((data) => {
                setReviews(data);
                setLoading(false);
            })
            .catch((err) => {
                setError(err.message);
                setLoading(false);
            });
    }, []);

    // ✅ 리뷰 3개씩 묶기 (최적화)
    const groupedReviews = useMemo(() => {
        const result = [];
        for (let i = 0; i < reviews.length; i += 3) {
            result.push(reviews.slice(i, i + 3));
        }
        return result;
    }, [reviews]);

    // ✅ 로딩/에러 처리
    if (loading) return <div className="review-slider">로딩 중...</div>;
    if (error) return <div className="review-slider">❌ {error}</div>;

    // ✅ 렌더링
    return (
        <div className="review-slider">
            <div
                className="slider-inner"
                style={{ transform: `translateY(-${currentSlide * 100}%)` }}
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
