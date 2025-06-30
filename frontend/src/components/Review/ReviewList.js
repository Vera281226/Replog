import { useEffect, useState, useCallback } from 'react';
import api from '../../api/axios';
import ReviewItem from './ReviewItem';

function ReviewList({ contentId, memberId }) {
  const [reviews, setReviews] = useState([]);
  const [sortType, setSortType] = useState('LATEST');

  // ✅ 리뷰 목록 조회 함수 (useCallback으로 감싸서 의존성 문제 해결)
  const fetchReviews = useCallback(async () => {
    try {
      const res = await api.get('/reviews', {
        params: {
          contentId: contentId || 1,
          memberId: memberId || 'testUser1',
          sortType,
        },
      });
      console.log('리뷰 응답:', res.data);
      setReviews(res.data);
    } catch (err) {
      console.error('리뷰 목록 불러오기 실패:', err);
    }
  }, [contentId, memberId, sortType]);

  // ✅ 초기 로딩 + 정렬 변경 시 리뷰 목록 조회
  useEffect(() => {
    fetchReviews();
  }, [fetchReviews]); // 🚫 더 이상 경고 없음

  // ✅ 팝업창 메시지 수신 (리뷰 작성 완료 시)
  useEffect(() => {
    const handleMessage = (event) => {
      if (event.data === 'reviewCreated') {
        console.log('📬 팝업에서 리뷰 작성 완료 수신');
        fetchReviews(); // 리뷰 목록 새로고침
      }
    };

    window.addEventListener('message', handleMessage);
    return () => window.removeEventListener('message', handleMessage);
  }, [fetchReviews]); // 💡 여기도 같이 넣어야 eslint 경고 없음

  // ✅ 팝업 열기 함수
  const handleOpenPopup = () => {
    window.open(
      '/review-popup',
      'ReviewPopup',
      'width=500,height=600,top=100,left=100'
    );
  };

  useEffect(() => {
    const handler = (e) => {
      if (e.data === 'reviewUpdated') {
        fetchReviews();
      }
    };
    window.addEventListener('message', handler);
    return () => window.removeEventListener('message', handler);
  }, []);


  return (
    <div className="mt-8">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold">리뷰 목록</h2>

        <div className="flex gap-2">
          <select
            value={sortType}
            onChange={(e) => setSortType(e.target.value)}
            className="border rounded px-2 py-1"
          >
            <option value="LATEST">최신순</option>
            <option value="RATING">별점 높은 순</option>
          </select>

          <button
            onClick={handleOpenPopup}
            className="bg-blue-500 text-white px-3 py-1 rounded"
          >
            리뷰 작성
          </button>
        </div>
      </div>

      {/* ✅ 리뷰 목록 출력 */}
      <ul className="space-y-4">
        {reviews
          .filter((r) => r.gnum === r.reviewId)
          .sort((a, b) => {
            if (a.memberId === memberId && b.memberId !== memberId) return -1;
            if (a.memberId !== memberId && b.memberId === memberId) return 1;
            if (sortType === 'RATING') {
              return (b.rating || 0) - (a.rating || 0);
            } else {
              return new Date(b.createdAt) - new Date(a.createdAt);
            }
          })
          .map((review) => (
            <ReviewItem
              key={review.reviewId}
              review={review}
              allReviews={reviews}
              onCommentAdded={fetchReviews}
              memberId={memberId}
            />
          ))}
      </ul>
    </div>
  );
}

export default ReviewList;
