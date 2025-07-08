import { useEffect, useState, useCallback, useRef } from 'react';
import api from '../../error/api/interceptor';
import ReviewItem from './ReviewItem';
import LoadingSpinner from '../common/LoadingSpinner';
import './ReviewList.css';

function ReviewList({ contentId, memberId, onCommentAdded, openModal }) {
  const [reviews, setReviews] = useState([]);
  const [sortType, setSortType] = useState('LATEST');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const loaderRef = useRef(null);

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

      setReviews((prev) => [...prev, ...newReviews]);

      if (newReviews.length < 5) {
        setHasMore(false);
      } else {
        setPage(currentPage + 1);
      }

      onCommentAdded?.();
    } catch (err) {
      console.error('리뷰 불러오기 실패:', err);
    } finally {
      const elapsed = Date.now() - start;
  const delay = Math.max(1000 - elapsed, 0); // 최소 800ms 유지
  setTimeout(() => setIsLoading(false), delay);
    }
  }, [contentId, memberId, sortType, page, isLoading, hasMore, onCommentAdded]);

  // 정렬 바뀌면 초기화 + 첫 페이지 로딩
  useEffect(() => {
    const init = async () => {
      setReviews([]);
      setPage(0);
      setHasMore(true);
      setIsLoading(true);
      try {
        const res = await api.get('/reviews', {
          params: { contentId, memberId, sortType, page: 0, size: 5 }
        });
        setReviews(res.data);
        if (res.data.length < 5) {
          setHasMore(false);
        } else {
          setPage(1);
        }
      } catch (err) {
        console.error('리뷰 초기 로딩 실패:', err);
      } finally {
        setIsLoading(false);
      }
    };

    init();
  }, [contentId, sortType]);

  // 무한 스크롤 감지
  useEffect(() => {
    const observer = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && hasMore && !isLoading) {
        fetchReviews(); // 현재 page로
      }
    }, { threshold: 1 });

    if (loaderRef.current) {
      observer.observe(loaderRef.current);
    }

    return () => {
      if (loaderRef.current) observer.unobserve(loaderRef.current);
    };
  }, [fetchReviews, hasMore, isLoading]);

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

      {reviews
        .filter((r) => r.gnum === r.reviewId)
        .map((review) => (
          <ReviewItem
            key={`review-${review.reviewId}`} // ✅ 중복 key 방지
            review={review}
            allReviews={reviews}
            onCommentAdded={() => {}}
            memberId={memberId}
          />
        ))}

      {isLoading && <LoadingSpinner />}
      <div ref={loaderRef} style={{ height: '1px' }} />
    </div>
  );
}

export default ReviewList;
