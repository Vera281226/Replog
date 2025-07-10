// src/components/MyReportList.js
import React, { useEffect, useState } from 'react';
import api from '../../error/api/interceptor';
import './MyReportList.css'

const typeToKorean = {
  USER: '회원 신고',
  POST: '게시글 신고',
  PARTY_POST: '모집글 신고',
  REVIEW: '리뷰 신고',
  CONTENT: '콘텐츠 신고',
  CHAT_MESSAGE: '채팅 신고',
  CONTENT_REQUEST: '요청'
};

function getTargetDisplay(targetType, targetTitle, targetId) {
  if (targetType === 'CONTENT_REQUEST') return '관리자';
  if (targetTitle) return targetTitle;
  return targetId;
}

export default function MyReportList({ onClose }) {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(10);

  useEffect(() => {
    setLoading(true);
    api.get(`/reports/my?page=${page}&size=${size}`, { withCredentials: true })
      .then(r => setReports(r.data.content))
      .catch(() => setErr('신고/요청 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, [page, size]);

  if (loading) return <div className="report-list">로딩 중…</div>;
  if (err) return <div className="report-list error">{err}</div>;
  if (!reports.length) return <div className="report-list">신고/요청 내역이 없습니다.</div>;

  return (
    <div className="report-list-modal">
      <div className="report-list-header">
        <h3>내 신고/요청 목록</h3>
        <button className="close-btn" onClick={onClose}>닫기</button>
      </div>
      <table className="report-table">
        <thead>
          <tr>
            <th>번호</th>
            <th>분류</th>
            <th>대상</th>
            <th>사유</th>
            <th>상태</th>
            <th>신고일</th>
          </tr>
        </thead>
        <tbody>
          {reports.map((r) => (
            <tr key={r.reportId}>
              <td>{r.reportId}</td>
              <td>{typeToKorean[r.targetType] || r.targetType}</td>
              <td>{getTargetDisplay(r.targetType, r.targetTitle, r.targetId)}</td>
              <td>{r.reason}</td>
              <td>{r.isProcessed ? '처리됨' : '대기중'}</td>
              <td>{r.createdAt?.slice(0, 10)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="pagination">
        <button disabled={page === 0} onClick={() => setPage(page - 1)}>이전</button>
        <span>{page + 1} 페이지</span>
        <button disabled={reports.length < size} onClick={() => setPage(page + 1)}>다음</button>
      </div>
    </div>
  );
}
