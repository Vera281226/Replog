// src/index/components/ReviewCard.js

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import axios from 'axios';
import { selectCurrentUser } from '../../error/redux/authSlice.js';

/**
 * ReviewCard 컴포넌트
 * -------------------------------------------------------------------
 * ○ 지금 뜨는 리뷰 섹션에서 사용하는 카드 컴포넌트
 * ○ Redux의 authSlice에서 로그인 사용자 정보 사용
 * ○ 좋아요 누르면 서버에 POST 요청 + 상태 반영
 * -------------------------------------------------------------------
 */
const ReviewCard = ({ review }) => {
    const navigate = useNavigate();

    // ✅ Redux 상태에서 로그인 사용자 정보 가져오기
    const currentUser = useSelector(selectCurrentUser);
    const loginUserId = currentUser?.memberId;

    const [liked, setLiked] = useState(false);
    const [likeCount, setLikeCount] = useState(review.likeCount || 0);

    // ✅ 좋아요 버튼 클릭
    const handleLikeClick = async () => {
        if (!loginUserId) {
            alert('로그인 후 이용해 주세요.');
            return;
        }

        try {
            await axios.post(
                `/api/reviews/${review.reviewId}/like`,
                { memberId: loginUserId },
                { withCredentials: true }
            );
            setLiked(!liked);
            setLikeCount(prev => liked ? prev - 1 : prev + 1);
        } catch (err) {
            alert('좋아요 요청 중 오류가 발생했습니다.');
        }
    };

    // ✅ 댓글 버튼 클릭 → 콘텐츠 리뷰 상세 페이지로 이동
    const handleCommentClick = () => {
        navigate(`/contents/${review.contentId}/reviews`);
    };

    return (
        <div className="review-card">
            {/* 상단: 작성자 + 별점 */}
            <div className="review-header">
                <span className="review-author">👤 {review.memberId}</span>
                <span className="review-rating">⭐ {review.rating}</span>
            </div>

            {/* 중단: 포스터 + 콘텐츠 제목 */}
            <div className="review-body">
                <img
                    src={
                        review.posterPath
                            ? `https://image.tmdb.org/t/p/w500${review.posterPath}`
                            : '/images/default-poster.png'
                    }
                    alt={review.contentTitle}
                    className="poster"
                    onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = '/images/default-poster.png';
                    }}
                />
                <span className="content-title">{review.contentTitle}</span>
            </div>

            {/* 리뷰 코멘트 */}
            <div className="comment">{review.cont}</div>

            {/* 하단: 좋아요 + 댓글 */}
            <div className="review-footer">
                <span className="like-button" onClick={handleLikeClick}>
                    {liked ? '❤️' : '🤍'} 좋아요 {likeCount}개
                </span>
                <span className="comment-button" onClick={handleCommentClick}>
                    💬 댓글
                </span>
            </div>
        </div>
    );
};

export default ReviewCard;
