// src/components/MyReviewList.js
import React, { useEffect, useState } from 'react';
import api from '../../error/api/interceptor';   // 경로는 프로젝트 구조에 맞추세요
import './MyReviewList.css';

export default function MyReviewList({ onClose }) {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(10);

  /* 내 리뷰 조회 */
  useEffect(() => {
    setLoading(true);
    api
      .get(`/reviews/my?page=${page}&size=${size}`, { withCredentials: true })
      .then(r => setReviews(r.data.content || []))
      .catch(() => setErr('리뷰 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, [page, size]);
  
  if (loading) return <div className="review-list">로딩 중…</div>;
  if (err)     return <div className="review-list error">{err}</div>;
  if (!reviews.length) return <div className="review-list">리뷰가 없습니다.</div>;

  /* 테이블 렌더링 */
  return (
    <div className="review-list-modal">
      <div className="review-list-header">
        <h3>내 리뷰 목록</h3>
        <button className="close-btn" onClick={onClose}>닫기</button>
      </div>

      <table className="review-table">
        <thead>
          <tr>
            <th>번호</th>
            <th>컨텐츠</th>
            <th>평점</th>
            <th>내용</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          {reviews.map(r => (
            <tr key={r.reviewId}>
              <td>{r.reviewId}</td>
              <td>{r.contentTitle || r.contentId}</td>
              <td>{r.rating ?? '-'}</td>
              <td>{r.cont?.length > 30 ? r.cont.slice(0, 30) + '…' : r.cont}</td>
              <td>{r.createdAt?.slice(0, 10)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="pagination">
        <button disabled={page === 0}              onClick={() => setPage(p => p - 1)}>이전</button>
        <span>{page + 1} 페이지</span>
        <button disabled={reviews.length < size}   onClick={() => setPage(p => p + 1)}>다음</button>
      </div>
    </div>
  );
}
