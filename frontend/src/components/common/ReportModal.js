// src/components/common/ReportModal.js
import React, { useState, useEffect } from 'react';
import axios from '../../error/api/interceptor';
import './ReportModal.css';

const ReportModal = ({ isOpen, onClose, targetType, targetId, isRequest = false }) => {
  const [formData, setFormData] = useState({
    reason: '',
    description: ''
  });
  const [loading, setLoading] = useState(false);

  // 모달 오픈 시 body 스크롤 잠금
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [isOpen]);

  // 신고/요청 사유 옵션
  const getReasonOptions = () => {
    if (isRequest) {
      return [
        '기능 개선 요청',
        '새로운 기능 제안',
        '버그 신고',
        '사용성 개선',
        '콘텐츠 추가 요청',
        '기타 요청'
      ];
    }

    // 신고 사유 (targetType별 다른 옵션 가능)
    const commonReasons = [
      '스팸 또는 광고',
      '욕설 또는 비방',
      '부적절한 내용',
      '저작권 침해',
      '개인정보 노출',
      '기타'
    ];

    const specificReasons = {
      'USER': ['부적절한 닉네임', '사기 의심', '괴롭힘'],
      'CHAT_MESSAGE': ['음란성 발언', '혐오 발언', '도배'],
      'PARTY_POST': ['허위 정보', '중복 모집', '모집 조건 위반'],
      'POST': ['가짜 뉴스', '혐오 표현'],
      'REVIEW': ['허위 리뷰', '영화와 무관한 내용']
    };

    return [...commonReasons, ...(specificReasons[targetType] || [])];
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.reason.trim()) {
      alert('사유를 선택해주세요.');
      return;
    }

    setLoading(true);
    
    try {
      const payload = {
        targetType: isRequest ? 'CONTENT_REQUEST' : targetType,
        targetId: targetId,
        reason: formData.reason,
        description: formData.description.trim() || null
      };

      await axios.post('/reports', payload, {
        withCredentials: true,
        timeout: 8000
      });

      alert(isRequest ? 
        '요청이 성공적으로 전송되었습니다! 검토 후 반영하겠습니다. 🙏' : 
        '신고가 접수되었습니다. 빠른 시일 내 검토하겠습니다. 📝'
      );
      
      onClose();
      
    } catch (error) {
      console.error('신고/요청 전송 실패:', error);
      
      if (error.response?.status === 401) {
        alert('로그인이 필요합니다.');
      } else {
        alert(isRequest ? '요청 전송에 실패했습니다.' : '신고 접수에 실패했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="report-modal-overlay">
      <div className="report-modal" onClick={e => e.stopPropagation()}>
        <div className="report-modal-header">
          <h3 className="report-modal-title">
            {isRequest ? '📝 개선 요청' : '🚨 신고하기'}
          </h3>
          <button className="report-modal-close" onClick={onClose}>✕</button>
        </div>

        <form className="report-modal-form" onSubmit={handleSubmit}>
          <div>
            <label className="report-modal-label">
              {isRequest ? '요청 사유' : '신고 사유'} *
            </label>
            <select
              className="report-modal-select"
              value={formData.reason}
              onChange={(e) => setFormData(prev => ({ ...prev, reason: e.target.value }))}
              required
            >
              <option value="">선택해주세요</option>
              {getReasonOptions().map((reason, index) => (
                <option key={index} value={reason}>{reason}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="report-modal-label">
              상세 설명 <span style={{ color: '#999', fontWeight: 'normal', fontSize: '12px' }}>(선택사항)</span>
            </label>
            <textarea
              className="report-modal-textarea"
              value={formData.description}
              onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
              placeholder={isRequest ? 
                '어떤 부분을 개선하면 좋을지 자세히 알려주세요...' : 
                '신고 사유에 대해 자세히 설명해주세요...'
              }
              maxLength={1000}
              rows={4}
            />
            <div className="report-modal-charcount">
              {formData.description.length}/1000
            </div>
          </div>

          <div className="report-modal-buttongroup">
            <button 
              type="button" 
              className="report-modal-cancel"
              onClick={onClose} 
              disabled={loading}
            >
              취소
            </button>
            <button 
              type="submit" 
              className="report-modal-submit"
              disabled={loading}
            >
              {loading ? '전송 중...' : (isRequest ? '요청 전송' : '신고 접수')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ReportModal;
