// src/components/admin/ReportAdminPage.js

import React, { useEffect, useState } from 'react';
import axios from '../error/api/interceptor';

const TARGET_TYPE_LABELS = {
  USER: '사용자',
  POST: '게시글',
  PARTY_POST: '파티 모집글',
  REVIEW: '리뷰',
  CONTENT: '콘텐츠',
  CHAT_MESSAGE: '채팅',
  CONTENT_REQUEST: '요청',
};

const TARGET_TYPES = Object.keys(TARGET_TYPE_LABELS);

const STATUS_TABS = [
  { key: 'unprocessed', label: '미처리' },
  { key: 'processed', label: '처리됨' },
];

function ReportAdminPage() {
  const [statusTab, setStatusTab] = useState('unprocessed');
  const [typeTab, setTypeTab] = useState('ALL');
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchReports();
    // eslint-disable-next-line
  }, [statusTab, typeTab]);

  const fetchReports = async () => {
    setLoading(true);
    try {
      let url = '/admin/reports';
      const params = { page: 0, size: 20 };
      if (statusTab === 'unprocessed') url += '/unprocessed';
      if (typeTab !== 'ALL') url = `/admin/reports/type/${typeTab}`;
      const { data } = await axios.get(url, { params, withCredentials: true });
      // data.content로 들어온다고 가정
      setReports(data.content || []);
    } catch (e) {
    }
    setLoading(false);
  };

  const handleProcess = (reportId) => {
    // 실제 처리 API 호출은 생략
    alert(`신고 처리 기능(미구현): ${reportId}`);
  };

  const handleDelete = (reportId) => {
    // 실제 삭제 API 호출은 생략
    alert(`신고 삭제 기능(미구현): ${reportId}`);
  };

  return (
    <div style={{ padding: 32 }}>
      <h2>신고 관리</h2>
      {/* 상태 탭 */}
      <div style={{ marginBottom: 16 }}>
        {STATUS_TABS.map(tab => (
          <button
            key={tab.key}
            onClick={() => setStatusTab(tab.key)}
            style={{
              fontWeight: statusTab === tab.key ? 'bold' : 'normal',
              marginRight: 8,
              padding: '6px 18px',
              borderRadius: 6,
              border: statusTab === tab.key ? '2px solid #1976d2' : '1px solid #aaa',
              background: statusTab === tab.key ? '#e3f2fd' : '#fff',
              cursor: 'pointer',
            }}
          >
            {tab.label}
          </button>
        ))}
      </div>
      {/* 종류 탭 */}
      <div style={{ marginBottom: 16 }}>
        <button
          onClick={() => setTypeTab('ALL')}
          style={{
            fontWeight: typeTab === 'ALL' ? 'bold' : 'normal',
            marginRight: 4,
            padding: '4px 12px',
            borderRadius: 4,
            border: typeTab === 'ALL' ? '2px solid #1976d2' : '1px solid #bbb',
            background: typeTab === 'ALL' ? '#e3f2fd' : '#fff',
            cursor: 'pointer',
          }}
        >전체</button>
        {TARGET_TYPES.map(type => (
          <button
            key={type}
            onClick={() => setTypeTab(type)}
            style={{
              fontWeight: typeTab === type ? 'bold' : 'normal',
              marginRight: 4,
              padding: '4px 12px',
              borderRadius: 4,
              border: typeTab === type ? '2px solid #1976d2' : '1px solid #bbb',
              background: typeTab === type ? '#e3f2fd' : '#fff',
              cursor: 'pointer',
            }}
          >
            {TARGET_TYPE_LABELS[type]}
          </button>
        ))}
      </div>
      {/* 리스트 */}
      {loading ? (
        <div>로딩 중...</div>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', background: '#fafafa' }}>
          <thead>
            <tr style={{ background: '#e3e3e3' }}>
              <th>번호</th>
              <th>상태</th>
              <th>종류</th>
              <th>신고자</th>
              <th>대상</th>
              <th>사유</th>
              <th>설명</th>
              <th>생성일</th>
              <th>처리일</th>
              <th>처리자</th>
              <th>액션</th>
            </tr>
          </thead>
          <tbody>
            {reports.length === 0 ? (
              <tr><td colSpan={11} style={{ textAlign: 'center', padding: 32 }}>신고 내역이 없습니다.</td></tr>
            ) : (
              reports.map((r, idx) => (
                <tr key={r.reportId} style={{ borderBottom: '1px solid #ddd' }}>
                  <td>{r.reportId}</td>
                  <td>
                    <span style={{
                      color: r.isProcessed ? '#388e3c' : '#d32f2f',
                      fontWeight: 'bold'
                    }}>
                      {r.isProcessed ? '처리됨' : '미처리'}
                    </span>
                  </td>
                  <td>{TARGET_TYPE_LABELS[r.targetType] || r.targetType}</td>
                  <td>{r.reporterNickname}</td>
                  <td>
                    {r.targetTitle && <b>{r.targetTitle}</b>}<br />
                    {r.targetContent && <span style={{ color: '#666', fontSize: 12 }}>{r.targetContent}</span>}<br />
                    {r.targetAuthor && <span style={{ color: '#888', fontSize: 11 }}>by {r.targetAuthor}</span>}
                  </td>
                  <td>{r.reason}</td>
                  <td>{r.description}</td>
                  <td>{r.createdAt?.slice(0, 16).replace('T', ' ')}</td>
                  <td>{r.processedAt ? r.processedAt.slice(0, 16).replace('T', ' ') : '-'}</td>
                  <td>{r.processorNickname || '-'}</td>
                  <td>
                    {!r.isProcessed && (
                      <button
                        onClick={() => handleProcess(r.reportId)}
                        style={{
                          background: '#1976d2',
                          color: '#fff',
                          border: 'none',
                          borderRadius: 4,
                          padding: '4px 10px',
                          marginBottom: 4,
                          cursor: 'pointer'
                        }}
                      >처리</button>
                    )}
                    <br />
                    <button
                      onClick={() => handleDelete(r.reportId)}
                      style={{
                        background: '#fff',
                        color: '#d32f2f',
                        border: '1px solid #d32f2f',
                        borderRadius: 4,
                        padding: '3px 10px',
                        cursor: 'pointer'
                      }}
                    >삭제</button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default ReportAdminPage;
