import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { logout } from '../../error/redux/authSlice';
import { useNavigate } from 'react-router-dom';
import api from '../../error/api/interceptor';
import InfoModal from '../../components/InfoModal';

export default function WithdrawModal({ onClose }) {
  const [pwd, setPwd] = useState('');
  const [loading, setLoading] = useState(false);

  // InfoModal 상태
  const [modal, setModal] = useState({
    isOpen: false,
    type: 'info', // 'info', 'error', 'success'
    title: '',
    message: '',
    onConfirm: null,
    onCancel: null,
    confirmLabel: '확인',
    cancelLabel: '취소'
  });

  const dispatch = useDispatch();
  const navigate = useNavigate();

  // InfoModal 열기
  const openModal = (props) => setModal({ ...modal, ...props, isOpen: true });
  // InfoModal 닫기
  const closeModal = () => setModal((m) => ({ ...m, isOpen: false }));

  // 탈퇴 폼 제출
  const handleWithdraw = async (e) => {
    e.preventDefault();
    if (!pwd.trim()) {
      openModal({
        type: 'warning',
        title: '입력 오류',
        message: '비밀번호를 입력해주세요.',
        onConfirm: closeModal,
        onCancel: closeModal,
        cancelLabel: undefined
      });
      return;
    }

    // 정말 탈퇴할지 확인
    openModal({
      type: 'warning',
      title: '정말로 탈퇴하시겠습니까?',
      message: '이 작업은 되돌릴 수 없습니다. 계속 진행하시겠습니까?',
      confirmLabel: '탈퇴하기',
      cancelLabel: '취소',
      onConfirm: async () => {
        closeModal();
        setLoading(true);
        try {
          const res = await api.post('/member/withdraw', { pwd }, { withCredentials: true });
          if (res.data && typeof res.data === 'string' && res.data.includes('완료')) {
            // 성공 안내 → 로그아웃 → 메인 이동
            openModal({
              type: 'success',
              title: '탈퇴 완료',
              message: '회원 탈퇴가 정상적으로 처리되었습니다.',
              onConfirm: async () => {
                closeModal();
                await dispatch(logout());
                navigate('/', { replace: true });
              },
              onCancel: async () => {
                closeModal();
                await dispatch(logout());
                navigate('/', { replace: true });
              },
              cancelLabel: undefined
            });
          } else {
            openModal({
              type: 'error',
              title: '탈퇴 실패',
              message: res.data || '탈퇴 처리에 실패했습니다.',
              onConfirm: closeModal,
              onCancel: closeModal,
              cancelLabel: undefined
            });
          }
        } catch (err) {
          openModal({
            type: 'error',
            title: '탈퇴 실패',
            message:
              err.response?.data ||
              '탈퇴 요청 중 오류가 발생했습니다.',
            onConfirm: closeModal,
            onCancel: closeModal,
            cancelLabel: undefined
          });
        } finally {
          setLoading(false);
        }
      },
      onCancel: closeModal
    });
  };

  return (
    <>
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
      {/* InfoModal */}
      <InfoModal
        isOpen={modal.isOpen}
        type={modal.type}
        title={modal.title}
        message={modal.message}
        confirmLabel={modal.confirmLabel}
        cancelLabel={modal.cancelLabel}
        onConfirm={modal.onConfirm}
        onCancel={modal.onCancel}
      />
    </>
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
