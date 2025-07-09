import React, { useState } from 'react';
import ReactModal from 'react-modal';
import InputWarning from "../../error/components/InputWarning";
import axios from '../../error/api/interceptor';
import './css/PasswordResetModal.css';
import InfoModal from "../InfoModal";
import { useNavigate } from 'react-router-dom';
import { validate } from "./validation";

// 모달 포커스 제어를 위한 기본 설정
ReactModal.setAppElement('#root');

const PasswordResetModal = ({ isOpen, onClose }) => {
  // 현재 단계: 1단계(이메일 인증) 또는 2단계(비밀번호 재설정)
  const [step, setStep] = useState(1);

  // 입력값 상태
  const [form, setForm] = useState({
    email: '',
    code: '',
    password: '',
    confirmPassword: ''
  });

  const [errors, setErrors] = useState({});

  // 인증번호 전송 결과
  const [sendResult, setSendResult] = useState({ type: '', message: '' });

  // 인증번호 확인 결과
  const [verifyResult, setVerifyResult] = useState({ type: '', message: '' });

  // 비밀번호 일치 확인 결과
  const [confirmResult, setConfirmResult] = useState({ type: '', message: '' });

  const [timeLeft, setTimeLeft] = useState(0);
  // 타이머 ID 저장(타이머 중지용)
  const [timerId, setTimerId] = useState(null);

  // 비밀번호 변경 결과 모달
  const [modal, setModal] = useState({
    isOpen: false,
    isSuccess: false,
    message: ""
  });

  // 페이지 이동용 훅
  const navigate = useNavigate();

  // InfoModal 닫기
  const handleModalClose = () => {
    setModal({ isOpen: false, isSuccess: false, message: "" });
  };

  // 확인 클릭 시 로그인 페이지로 이동
  const handleGoToLogin = async () => {
    handleModalClose();
    await new Promise(resolve => setTimeout(resolve, 100));
    navigate("/login");
  };

  // 입력값 변경 핸들러
  const handleChange = (e) => {
    const { name, value } = e.target;
    const updatedForm = { ...form, [name]: value };

    setForm(updatedForm);

    // 입력할 때마다 형식 검사
    if (validate[name]) { 
      setErrors((prev) => ({ ...prev, [name]: validate[name](value) })); 
    }

    // 비밀번호 일치 검사
    if (name === 'password' || name === 'confirmPassword') {
      const confirmError =
        updatedForm.password !== updatedForm.confirmPassword
          ? '새 비밀번호가 일치하지 않습니다.'
          : '';
      setConfirmResult({
        type: confirmError ? 'error' : 'success',
        message: confirmError || '비밀번호가 일치합니다.'
      });
    }
  };

  // 인증번호 이메일 발송
  const sendCode = async () => {
    try {
      await axios.post('/email/send', { email: form.email });
      setSendResult({ type: 'success', message: '인증코드가 이메일로 전송되었습니다.' });
      setTimeLeft(300);
      if (timerId) clearInterval(timerId);  // 기존 타이머 제거

      // 1초마다 timeLeft를 감소시키는 하나의 타이머를 시작
      const id = setInterval(() => {
        setTimeLeft(prev => {
          if (prev <= 1) {
            clearInterval(id);
            return 0;
          }
          return prev -1;
        });
      }, 1000);
      setTimerId(id);
    } catch (err) {
      setSendResult({ type: 'error', message: err.response?.data || '이메일 전송 실패' });
    }
  };

  // 초를 "MM:SS" 문자열로 변환하는 함수
  const formatTime = (seconds) => {
    const m = String(Math.floor(seconds/60)).padStart(2, '0'); // 분 계산 후 2자리 문자열
    const s = String(seconds % 60).padStart(2, '0'); // 초 계산 후 2자리 문자열
    return `${m}:${s}`;
  };

  // 인증번호 확인
  const verifyCode = async () => {
    try {
      await axios.post('/email/verify', {
        email: form.email,
        authCode: form.code
      });
      setStep(2); // 다음 단계로 이동
      setVerifyResult({
        type: 'success',
        message: '인증에 성공했습니다. 새 비밀번호를 입력하세요.'
      });
    } catch (err) {
      setVerifyResult({
        type: 'error',
        message: err.response?.data || '인증 실패: 인증번호가 일치하지 않습니다.'
      });
      setForm(prev => ({ ...prev, code: '' })); // 인증 실패 시 코드 입력칸 초기화
    }
  };

  // 비밀번호 변경 요청
  const resetPassword = async () => {
      // 비밀번호 형식 검사
      const passwordError = validate.password(form.password);
      const confirmError =
        form.password !== form.confirmPassword
          ? "새 비밀번호가 일치하지 않습니다."
          : "";

      setErrors(prev => ({
        ...prev,
        password: passwordError,
        confirmPassword: confirmError
      }));

      if (passwordError || confirmError) return;
    
  //  // 비밀번호 일치 검사
  //   if (form.password !== form.confirmPassword) {
  //     setConfirmResult({
  //       type: 'error',
  //       message: '새 비밀번호가 일치하지 않습니다.'
  //     });
  //     return;
  //   }

    try {
      await axios.post('/email/reset-password', {
        email: form.email,
        password: form.password
      });

      // 성공 시 모달 띄우고 리셋
      setModal({
        isOpen: true,
        isSuccess: true,
        message: "비밀번호가 변경되었습니다."
      });

      // 1.5초 후 입력값 및 상태 초기화
      setTimeout(() => {
        
        setStep(1);
        setForm({
          email: '',
          code: '',
          password: '',
          confirmPassword: ''
        });
        setSendResult({ type: '', message: '' });
        setVerifyResult({ type: '', message: '' });
        setConfirmResult({ type: '', message: '' });
        onClose();
      }, 1500);
    } catch (err) {
      // 에러 메시지 추출
      const errorMessage = typeof err.response?.data === "object"
        ? err.response.data.message || JSON.stringify(err.response.data)
        : err.response?.data || "비밀번호 변경에 실패했습니다.";

      // 실패 모달 표시
      setModal({
        isOpen: true,
        isSuccess: false,
        message: errorMessage
      });
    }
  };

  return (
    <ReactModal
      isOpen={isOpen}
      onRequestClose={onClose}
      className="pw-modal"
      overlayClassName="pw-modal__overlay"
    >
      {/* 모달 헤더 */}
      <div className="pw-modal__header">
        <h2>비밀번호 재설정</h2>
      </div>

      {/* 모달 본문 */}
      <div className="pw-modal__body">
        <h4>비밀번호를 잊으셨나요?</h4>
        <p>
          계정의 이메일 주소를 입력해주세요.<br />
          비밀번호 재설정 링크가 포함된 메일이 발송됩니다.
        </p>

        {/* 1단계: 이메일 인증 */}
        {step === 1 && (
          <>
            <div className="form-group">
              <input
                className="mr-10"
                id="email"
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="이메일"
              />
              <button type="button" onClick={sendCode} className="mr-10">
                인증번호 발송
              </button>
              {/* 인증번호 전송 결과 메시지 */}
              {sendResult.type === 'success' && timeLeft > 0 && (
                <p className={`pw-message ${sendResult.type}`}>
                  {sendResult.message}
                  <span className="expire-text"> (남은 시간: {formatTime(timeLeft)})</span>
                </p>
              )}

              {sendResult.type === 'success' && timeLeft === 0 && (
                <p className="pw-message error">
                  인증 시간이 만료되었습니다. 인증번호를 다시 발급받으세요.
                </p>
              )}

              {sendResult.type === 'error' && sendResult.message && (
                <p className="pw-message error">{sendResult.message}</p>
              )}
            </div>

            <div className="form-group">
              <input
                className="mr-10"
                id="code"
                type="text"
                name="code"
                value={form.code}
                onChange={handleChange}
                placeholder="인증번호 입력"
              />
              <button type="button" onClick={verifyCode} disabled={timeLeft === 0}>인증번호 확인</button>

              {/* 인증 확인 결과 메시지 */}
              {verifyResult.message && (
                <p className={`pw-message ${verifyResult.type}`}>
                  {verifyResult.message}
                </p>
              )}
            </div>
          </>
        )}

        {/* 2단계: 비밀번호 재설정 */}
        {step === 2 && (
          <>
            {verifyResult.message && (
              <p className={`pw-message ${verifyResult.type}`}>
                {verifyResult.message}
              </p>
            )}

            <div className="form-group">
              <label htmlFor="password">새 비밀번호 입력</label>
              <input
                className="ml-10"
                id="password"
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
              />
              {errors.password && <InputWarning message={errors.password} />}
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">새 비밀번호 확인</label>
              <input
                className="ml-10"
                id="confirmPassword"
                type="password"
                name="confirmPassword"
                value={form.confirmPassword}
                onChange={handleChange}
              />
              {errors.password && <InputWarning message={errors.password} />}
            </div>

            {/* 비밀번호 일치 검사 결과 메시지 */}
            {confirmResult.message && (
              <p className={`pw-message ${confirmResult.type}`}>
                {confirmResult.message}
              </p>
            )}

            <button type="button" onClick={resetPassword} disabled={errors.password || errors.confirmPassword || confirmResult.message !== '비밀번호가 일치합니다.'}>변경하기</button>
            <button type="button" onClick={onClose} className="ml-10">취소</button>
          </>
        )}
      </div>

      {/* 모달 하단 버튼 */}
      <div className="pw-modal__footer">
        <button
          type="button"
          className="pw-modal__btn pw-modal__btn--close"
          onClick={onClose}
        >
          닫기
        </button>
      </div>

      {/* 성공 모달 */}
      {modal.isOpen && modal.isSuccess && (
        <InfoModal
          isOpen={modal.isOpen}
          type="success"
          title="비밀번호 변경 완료"
          message={modal.message}
          confirmLabel="확인"
          cancelLabel="닫기"
          onConfirm={handleGoToLogin}
          onCancel={handleModalClose}
        />
      )}

      {/* 실패 모달 */}
      {modal.isOpen && !modal.isSuccess && (
        <InfoModal
          isOpen={modal.isOpen}
          type="error"
          title="비밀번호 변경 실패"
          message={modal.message}
          confirmLabel="확인"
          cancelLabel="닫기"
          onConfirm={handleModalClose}
          onCancel={handleModalClose}
        />
      )}
    </ReactModal>
  );
};

export default PasswordResetModal;