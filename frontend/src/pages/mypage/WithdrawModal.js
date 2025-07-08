// src/pages/mypage/WithdrawModal.js

import React, { useState } from 'react';
import api from '../../error/api/interceptor';

export default function WithdrawModal({ onClose, onSuccess }) {
  const [pwd, setPwd] = useState('');
  const [loading, setLoading] = useState(false);

  const handleWithdraw = async (e) => {
    e.preventDefault();
    if (!pwd.trim()) {
      alert('비밀번호를 입력해주세요.');
      return;
    }
    if (!window.confirm('정말로 회원 탈퇴를 진행하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return;

    setLoading(true);
    try {
      const res = await api.post('/member/withdraw', { pwd }, { withCredentials: true });
      if (res.data && typeof res.data === 'string' && res.data.includes('완료')) {
        onSuccess();
      } else {
        alert(res.data || '탈퇴 처리에 실패했습니다.');
      }
    } catch (err) {
      if (err.response?.data) {
        alert(err.response.data);
      } else {
        alert('탈퇴 요청 중 오류가 발생했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.overlay} onClick={onClose}>
      <div style={styles.modal} onClick={e => e.stopPropagation()}>
        <h3 style={{ margin: 0, marginBottom: 20 }}>회원 탈퇴</h3>
        <form onSubmit={handleWithdraw}>
          <div style={{ marginBottom: 16 }}>
            <label>
              비밀번호 입력 <span style={{ color: '#dc3545' }}>*</span>
              <input
                type="password"
                value={pwd}
                onChange={e => setPwd(e.target.value)}
                style={styles.input}
                disabled={loading}
                autoFocus
              />
            </label>
          </div>
          <div style={{ color: '#888', fontSize: 13, marginBottom: 16 }}>
            탈퇴 시 아이디와 닉네임을 제외한 모든 정보가 삭제됩니다.<br />
            이 작업은 되돌릴 수 없습니다.
          </div>
          <div style={{ display: 'flex', gap: 8 }}>
            <button
              type="button"
              style={styles.cancel}
              onClick={onClose}
              disabled={loading}
            >취소</button>
            <button
              type="submit"
              style={styles.submit}
              disabled={loading}
            >{loading ? '처리 중...' : '탈퇴하기'}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

const styles = {
  overlay: {
    position: 'fixed', left: 0, top: 0, right: 0, bottom: 0,
    background: 'rgba(0,0,0,0.4)', zIndex: 1000,
    display: 'flex', alignItems: 'center', justifyContent: 'center'
  },
  modal: {
    background: '#fff', borderRadius: 8, padding: 24, minWidth: 320, maxWidth: 400
  },
  input: {
    width: '100%', padding: 8, marginTop: 6, borderRadius: 4, border: '1px solid #ccc'
  },
  cancel: {
    flex: 1, padding: 10, border: '1px solid #ccc', borderRadius: 4, background: '#fff', cursor: 'pointer'
  },
  submit: {
    flex: 1, padding: 10, border: 'none', borderRadius: 4, background: '#dc3545', color: '#fff', fontWeight: 'bold', cursor: 'pointer'
  }
};
