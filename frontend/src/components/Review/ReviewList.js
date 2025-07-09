import { useEffect, useState, useCallback, useRef } from 'react';
import api from '../../error/api/interceptor';
import ReviewItem from './ReviewItem';
import LoadingSpinner from '../common/LoadingSpinner';
import './ReviewList.css';

function ReviewList({ contentId, memberId, onCommentAdded, openModal, onReviewUpdated }) {
  const [reviews, setReviews] = useState([]);
  const [sortType, setSortType] = useState('LATEST');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const loaderRef = useRef(null);

  const initReviews = useCallback(async () => {
    setIsLoading(true);
    setPage(0);
    setHasMore(true);
    try {
      const res = await api.get('/reviews', {
        params: { contentId, memberId, sortType, page: 0, size: 5 }
      });

      const newReviews = res.data;

      const sorted = newReviews.sort((a, b) => {
        if (a.memberId === memberId && b.memberId !== memberId) return -1;
        if (a.memberId !== memberId && b.memberId === memberId) return 1;
        return 0;
      });

      setReviews(sorted);

      if (newReviews.length < 5) setHasMore(false);
      else setPage(1);

    } catch (err) {
      console.error('리뷰 초기 로딩 실패:', err);
    } finally {
      setIsLoading(false);
    }
  }, [contentId, memberId, sortType]);

  const fetchReviews = useCallback(async (currentPage = page) => {
    if (isLoading || !hasMore) return;
    setIsLoading(true);
    const start = Date.now();
    try {
      const res = await api.get('/reviews', {
        params: {
          contentId,
          memberId,
          sortType,
          page: currentPage,
          size: 5,
        },
      });

      const newReviews = res.data;

      const merged = [...reviews, ...newReviews];

      const sorted = merged.sort((a, b) => {
        if (a.memberId === memberId && b.memberId !== memberId) return -1;
        if (a.memberId !== memberId && b.memberId === memberId) return 1;
        return 0;
      });

      setReviews(sorted);

      if (newReviews.length < 5) setHasMore(false);
      else setPage(currentPage + 1);

    } catch (err) {
      console.error('리뷰 추가 로딩 실패:', err);
    } finally {
      const elapsed = Date.now() - start;
      const delay = Math.max(800 - elapsed, 0);
      setTimeout(() => setIsLoading(false), delay);
    }
  }, [contentId, memberId, sortType, page, isLoading, hasMore, reviews]);

  useEffect(() => {
    initReviews();
  }, [initReviews]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMore && !isLoading) {
          fetchReviews();
        }
      },
      { threshold: 1 }
    );

    if (loaderRef.current) observer.observe(loaderRef.current);
    return () => {
      if (loaderRef.current) observer.unobserve(loaderRef.current);
    };
  }, [fetchReviews, hasMore, isLoading]);

  const sortedReviews = reviews
    .filter((r) => r.gnum === r.reviewId)
    .sort((a, b) => {
      if (a.memberId === memberId && b.memberId !== memberId) return -1;
      if (a.memberId !== memberId && b.memberId === memberId) return 1;
      return 0;
    });

  const handleOpenModal = () => {
    openModal(() => {
      initReviews();
      onReviewUpdated?.(); // 평점도 갱신
    });
  };

  return (
    <div className="review-list">
      <div className="review-list-header">
        <h2>리뷰 목록</h2>
        <div className="review-list-controls">
          <button className="btn-deep-purple" onClick={handleOpenModal}>
            리뷰 작성
          </button>
          <select value={sortType} onChange={(e) => setSortType(e.target.value)}>
            <option value="LATEST">최신순</option>
            <option value="RATING">별점 높은 순</option>
          </select>
        </div>
      </div>

      {sortedReviews.map((review) => (
        <ReviewItem
          key={`review-${review.reviewId}`}
          review={review}
          allReviews={reviews}
          onCommentAdded={initReviews}
          memberId={memberId}
        />
      ))}

      {isLoading && <LoadingSpinner />}
      <div ref={loaderRef} style={{ height: '1px' }} />
    </div>
  );
}

export default ReviewList;
