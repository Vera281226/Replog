// src/components/admin/ReportAdminPage.js
import React, { useEffect, useState, useCallback } from 'react';
import axios from '../error/api/interceptor';
import InfoModal from '../components/InfoModal';
import './ReportAdminPage.css';

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

  // InfoModal 상태
  const [modal, setModal] = useState({
    isOpen: false,
    type: 'info',
    title: '',
    msg: '',
    onOk: () => {},
  });

  const openModal = useCallback((type, title, msg, onOk) => {
    setModal({ isOpen: true, type, title, msg, onOk });
  }, []);

  const closeModal = () => setModal(m => ({ ...m, isOpen: false }));

  // 신고 목록 조회
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
      setReports(data.content || []);
    } catch (e) {
      console.error('신고 목록 조회 실패:', e);
    }
    setLoading(false);
  };

  // 에러 메시지 안전 처리 함수
  const getErrorMessage = (e, fallback) => {
    if (typeof e?.response?.data === 'object' && e?.response?.data !== null) {
      return e.response.data.message || fallback;
    }
    return e?.response?.data || fallback;
  };

  // 삭제 및 처리 (USER/CONTENT_REQUEST 예외 처리)
  const handleProcessAndDelete = (reportId, targetType) => {
    if (targetType === 'USER') {
      openModal('error', '불가', 'USER는 삭제할 수 없습니다.', closeModal);
      return;
    }
    if (targetType === 'CONTENT_REQUEST') {
      openModal(
        'warning',
        '신고 처리',
        '요청 유형은 삭제할 데이터가 없으므로 신고만 처리합니다.',
        async () => {
          try {
            await axios.put(`/admin/reports/${reportId}/process`, null, { withCredentials: true });
            openModal('success', '처리 완료', '신고가 처리되었습니다.', () => {
              closeModal();
              fetchReports();
            });
          } catch (e) {
            openModal('error', '처리 실패', getErrorMessage(e, '처리에 실패하였습니다.'), closeModal);
          }
        }
      );
      return;
    }
    openModal(
      'warning',
      '신고 처리 및 삭제',
      '해당 콘텐츠를 삭제하고 신고를 처리하시겠습니까?',
      async () => {
        try {
          await axios.put(`/admin/reports/${reportId}/process-delete`, null, { withCredentials: true });
          openModal('success', '처리 완료', '신고가 처리되고 콘텐츠가 삭제되었습니다.', () => {
            closeModal();
            fetchReports();
          });
        } catch (e) {
          openModal('error', '처리 실패', getErrorMessage(e, '처리에 실패하였습니다.'), closeModal);
        }
      }
    );
  };

  // 신고 처리 (원본 삭제 없음, 요청/USER 제외)
  const handleProcess = (reportId, targetType) => {
    if (targetType === 'CONTENT_REQUEST') {
      handleProcessAndDelete(reportId, targetType);
      return;
    }
    openModal(
      'warning',
      '신고 처리',
      '해당 신고를 처리하시겠습니까?\n(원본 콘텐츠는 삭제되지 않습니다)',
      async () => {
        try {
          await axios.put(`/admin/reports/${reportId}/process`, null, { withCredentials: true });
          openModal('success', '처리 완료', '신고가 처리되었습니다.', () => {
            closeModal();
            fetchReports();
          });
        } catch (e) {
          openModal('error', '처리 실패', getErrorMessage(e, '처리에 실패하였습니다.'), closeModal);
        }
      }
    );
  };

  // 처리 취소
  const handleCancel = (reportId) => {
    openModal(
      'warning',
      '처리 취소',
      '해당 신고의 처리 상태를 취소하시겠습니까?',
      async () => {
        try {
          await axios.put(`/admin/reports/${reportId}/cancel`, null, { withCredentials: true });
          openModal('success', '취소 완료', '처리 상태가 취소되었습니다.', () => {
            closeModal();
            fetchReports();
          });
        } catch (e) {
          openModal('error', '취소 실패', getErrorMessage(e, '취소에 실패하였습니다.'), closeModal);
        }
      }
    );
  };

  return (
    <div className="report-admin-page">
      <h2>신고 관리</h2>
      
      {/* 상태 탭 */}
      <div className="report-admin-tabs">
        {STATUS_TABS.map(tab => (
          <button
            key={tab.key}
            className={statusTab === tab.key ? 'active' : ''}
            onClick={() => setStatusTab(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* 종류 탭 */}
      <div className="report-admin-types">
        <button
          className={typeTab === 'ALL' ? 'active' : ''}
          onClick={() => setTypeTab('ALL')}
        >
          전체
        </button>
        {TARGET_TYPES.map(type => (
          <button
            key={type}
            className={typeTab === type ? 'active' : ''}
            onClick={() => setTypeTab(type)}
          >
            {TARGET_TYPE_LABELS[type]}
          </button>
        ))}
      </div>

      {/* 리스트 */}
      {loading ? (
        <div className="report-admin-loading">로딩 중...</div>
      ) : (
        <table className="report-admin-table">
          <thead>
            <tr>
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
              <tr>
                <td colSpan={11} className="report-admin-empty">
                  신고 내역이 없습니다.
                </td>
              </tr>
            ) : (
              reports.map((r) => (
                <tr key={r.reportId}>
                  <td>{r.reportId}</td>
                  <td>
                    <span className={r.isProcessed ? 'status-processed' : 'status-unprocessed'}>
                      {r.isProcessed ? '처리됨' : '미처리'}
                    </span>
                  </td>
                  <td>{TARGET_TYPE_LABELS[r.targetType] || r.targetType}</td>
                  <td>{r.reporterNickname || r.reporterId}</td>
                  <td className="report-admin-target">
                    {r.targetTitle && (
                      <div className="target-title">
                        {r.targetTitle}
                      </div>
                    )}
                    {r.targetContent && (
                      <div className="target-content">
                        {r.targetContent.length > 50 
                          ? r.targetContent.substring(0, 50) + '...' 
                          : r.targetContent}
                      </div>
                    )}
                    {r.targetAuthor && (
                      <div className="target-author">
                        by {r.targetAuthor}
                      </div>
                    )}
                  </td>
                  <td>{r.reason}</td>
                  <td className="report-admin-desc">
                    {r.description ? (
                      r.description.length > 30 
                        ? r.description.substring(0, 30) + '...' 
                        : r.description
                    ) : '-'}
                  </td>
                  <td className="report-admin-date">
                    {r.createdAt ? r.createdAt.slice(0, 16).replace('T', ' ') : '-'}
                  </td>
                  <td className="report-admin-date">
                    {r.processedAt ? r.processedAt.slice(0, 16).replace('T', ' ') : '-'}
                  </td>
                  <td>{r.processorNickname || '-'}</td>
                  <td>
                    {!r.isProcessed ? (
                      <>
                        {/* 삭제 및 처리 버튼: USER/CONTENT_REQUEST 제외 */}
                        {r.targetType !== 'USER' && r.targetType !== 'CONTENT_REQUEST' && (
                          <button
                            className="report-admin-action-btn delete"
                            onClick={() => handleProcessAndDelete(r.reportId, r.targetType)}
                          >
                            삭제 및 처리
                          </button>
                        )}
                        {/* 요청은 그냥 처리만 */}
                        {r.targetType === 'CONTENT_REQUEST' && (
                          <button
                            className="report-admin-action-btn"
                            onClick={() => handleProcessAndDelete(r.reportId, r.targetType)}
                          >
                            처리
                          </button>
                        )}
                        {/* 일반 처리 버튼: USER만 */}
                        {r.targetType === 'USER' && (
                          <button
                            className="report-admin-action-btn"
                            onClick={() => handleProcess(r.reportId, r.targetType)}
                          >
                            처리
                          </button>
                        )}
                      </>
                    ) : (
                      <button
                        className="report-admin-action-btn cancel"
                        onClick={() => handleCancel(r.reportId)}
                      >
                        처리 취소
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}

      {/* InfoModal 공통 사용 */}
      <InfoModal
        isOpen={modal.isOpen}
        type={modal.type}
        title={modal.title}
        message={modal.msg}
        confirmLabel="확인"
        cancelLabel="취소"
        onConfirm={() => { modal.onOk(); }}
        onCancel={closeModal}
      />
    </div>
  );
}

export default ReportAdminPage;
