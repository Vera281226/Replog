// src/components/member/MemberForm.js
import React, { useState, useEffect } from "react";
import axios from "../../error/api/interceptor";
import { useNavigate } from "react-router-dom";
import InputWarning from "../../error/components/InputWarning";
import AddressModal from "./AddressModal";
import GenreSelect from "./GenreSelect";
import InfoModal from "../InfoModal";
import { validate } from "./validation";
import "./css/MemberRegisterForm.css";

const defaultFormData = {
  memberId: "",
  password: "",
  name: "",
  nickname: "",
  email: "",
  phone: "",
  address: "",
  birthdate: "",
  gender: "",
  genres: [],
};

export default function MemberForm({ mode = "register" }) {
  const navigate = useNavigate();
  const [formData, setFormData] = useState(defaultFormData);
  const [errors, setErrors] = useState({});
  const [isAddressModalOpen, setAddressModalOpen] = useState(false);
  const [modal, setModal] = useState({
    isOpen: false,
    isSuccess: false,
    message: ""
  });
  const [loading, setLoading] = useState(mode === "edit"); // 수정모드면 로딩부터

  const [idCheckMessage, setIdCheckMessage] = useState('');
  const [nicknameCheckMessage, setNicknameCheckMessage] = useState('');
  const [emailSendMessage, setEmailSendMessage] = useState('');

  const [emailCode, setEmailCode] = useState('');
  const [verifyResult, setVerifyResult] = useState('');
  const [isCodeSent, setIsCodeSent] = useState(false); // 인증코드 발송 여부

  // 회원정보 수정 모드일 때, 현재 회원 데이터 불러오기
  useEffect(() => {
    if (mode === "edit") {
      setLoading(true);
      axios.get("/member/info", { withCredentials: true })
        .then(res => {
          const d = res.data;
          setFormData({
            memberId: d.memberId || "",
            password: "",
            name: d.name || "",
            nickname: d.nickname || "",
            email: d.email || "",
            phone: d.phone || "",
            address: d.address || "",
            birthdate: d.birthdate || "",
            gender: d.gender || "",
            genres: (d.genres || []).map(g => ({ value: g, label: g }))
          });
        })
        .catch(e => {
          setModal({
            isOpen: true,
            isSuccess: false,
            message: "회원 정보를 불러오지 못했습니다. 로그인 상태를 확인해주세요."
          });
        })
        .finally(() => setLoading(false));
    }
  }, [mode]);

  // 입력 핸들러
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: validate[name](value) }));
    }
  };

  // 장르 선택
  const handleGenreChange = (selected) => {
    setFormData(prev => ({ ...prev, genres: selected }));
    if (errors.genres) {
      setErrors(prev => ({ ...prev, genres: validate.genres(selected) }));
    }
  };

  // 모달 닫기
  const handleModalClose = () => {
    setModal({ isOpen: false, isSuccess: false, message: "" });
  };

  // 로그인/메인 이동
  const handleGoToLogin = async () => {
    handleModalClose();
    await new Promise(resolve => setTimeout(resolve, 100));
    navigate("/login");
  };
  const handleGoToMain = async () => {
    handleModalClose();
    await new Promise(resolve => setTimeout(resolve, 100));
    navigate("/");
  };

  // 아이디 중복 체크
  const checkDuplicateId = async () => {
    try {
      const response = await axios.post('/member/id-check', { memberId: formData.memberId });
      setIdCheckMessage(response.data);
    } catch (err) {
      const message = err.response?.data?.message || '아이디 중복 확인 중 오류가 발생했습니다.';
      setIdCheckMessage(message);
    }
  };

  // 닉네임 중복 체크
  const checkDuplicateNickname = async () => {
    try {
      const response = await axios.post('/member/nickname-check', { nickname: formData.nickname });
      setNicknameCheckMessage(response.data);
    } catch (err) {
      const message = err.response?.data?.message || '닉네임 중복 확인 중 오류가 발생했습니다.';
      setNicknameCheckMessage(message);
    }
  };

  // 인증 코드 전송
  const sendEmailCode = async () => {
    try {
      await axios.post('/email/send', { email: formData.email });
      setEmailSendMessage('인증코드가 전송되었습니다.');
      setIsCodeSent(true); 
    } catch (err) {
      setEmailSendMessage(err.response?.data || '이메일 전송 실패');
      setIsCodeSent(false); 
    }
  };

  const handleCodeChange = (e) => {
    setEmailCode(e.target.value);
  };

  const verifyEmailCode = async () => {
    try {
      await axios.post('/email/verify', {
        email: formData.email,
        authCode: String(emailCode)
      });
      setIsEmailVerified(true);
      setVerifyResult('인증이 완료되었습니다. 회원가입을 계속 진행해주세요.');
    } catch (err) {
      setVerifyResult(err.response?.data || '인증 실패');
      setIsEmailVerified(false);
    }
  };

  // 제출
  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = {};
    Object.keys(formData).forEach((key) => {
      newErrors[key] =
        key === "genres"
          ? validate.genres(formData.genres)
          : validate[key](formData[key]);
    });
    setErrors(newErrors);
    if (Object.values(newErrors).some((v) => v)) return;

    const payload = {
      ...formData,
      genres: formData.genres.map((g) => g.value),
    };

    try {
      if (mode === "register") {
        await axios.post("/member/signup", payload, { withCredentials: true });
        setModal({
          isOpen: true,
          isSuccess: true,
          message: "회원가입이 완료되었습니다!"
        });
      } else {
        await axios.post("/member/update", payload, { withCredentials: true });
        setModal({
          isOpen: true,
          isSuccess: true,
          message: "회원정보가 수정되었습니다!"
        });
      }
    } catch (err) {
      const errorMessage = typeof err.response?.data === "object"
        ? err.response.data.message || JSON.stringify(err.response.data)
        : err.response?.data || "처리에 실패했습니다.";
      setModal({
        isOpen: true,
        isSuccess: false,
        message: errorMessage
      });
    }
  };

  if (loading) return <div style={{ padding: 40 }}>회원 정보를 불러오는 중입니다...</div>;

  return (
    <div className="member-register-form">
      <h2>{mode === "register" ? "회원가입" : "회원정보 수정"}</h2>
      <form onSubmit={handleSubmit}>
        {/* 아이디 */}
        <div>
          <label htmlFor="memberId">아이디</label>
          <div style={{ display: 'flex', gap: '8px' }}>
          <input
            id="memberId"
            name="memberId"
            value={formData.memberId}
            onChange={handleChange}
            placeholder="아이디"
            readOnly={mode === "edit"}
            style={mode === "edit" ? { background: "#e9ecef", color: "#495057" } : {}}
          />
          <button type="button" onClick={checkDuplicateId}>중복확인</button>
          </div>
          {errors.memberId && <InputWarning message={errors.memberId} />}
          {idCheckMessage && <div className="info-message">{idCheckMessage}</div>}
        </div>
        {/* 비밀번호 */}
        <div>
          <label htmlFor="password">비밀번호</label>
          <input
            id="password"
            type="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            placeholder="비밀번호"
          />
          {errors.password && <InputWarning message={errors.password} />}
        </div>
        {/* 이름 */}
        <div>
          <label htmlFor="name">이름</label>
          <input
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="이름"
          />
          {errors.name && <InputWarning message={errors.name} />}
        </div>
        {/* 닉네임 */}
        <div>
          <label htmlFor="nickname">닉네임</label>
          <div style={{ display: 'flex', gap: '8px' }}>
          <input
            id="nickname"
            name="nickname"
            value={formData.nickname}
            onChange={handleChange}
            placeholder="닉네임"
          />
          <button type="button" onClick={checkDuplicateNickname}>중복확인</button>
          </div>
          {errors.nickname && <InputWarning message={errors.nickname} />}
          {nicknameCheckMessage && <div className="info-message">{nicknameCheckMessage}</div>}
        </div>
        {/* 이메일 */}
        <div>
          <label htmlFor="email">이메일</label>
          <div style={{ display: 'flex', gap: '8px' }}>
          <input
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="example@mail.com"
          />
          <button type="button" onClick={sendEmailCode}>인증코드 발송</button>
          </div>
          {errors.email && <InputWarning message={errors.email} />}
          {emailSendMessage && <div className="info-message">{emailSendMessage}</div>}
        </div>
        {/* 인증코드 입력 + 확인 */}
        {isCodeSent && !isEmailVerified &&(
        <div className={`verify-box ${isEmailVerified ? 'fade-out-up' : ''}`}>
          <label htmlFor="emailCode">인증코드 입력</label>
          <div style={{ display: 'flex', gap: '8px' }}>
            <input
              id="emailCode"
              name="emailCode"
              value={emailCode}
              onChange={handleCodeChange}
              placeholder="인증코드 입력"
            />
            <button type="button" onClick={verifyEmailCode}>인증코드 확인</button>
          </div>
        </div>
        {/* 인증 결과 메세지 */}
        {verifyResult && (
        <div className={`info-message ${isEmailVerified ? 'verified' : 'error'}`}>
          {verifyResult}
        </div>
        )}
        )}
        {/* 휴대폰 */}
        <div>
          <label htmlFor="phone">휴대폰</label>
          <input
            id="phone"
            name="phone"
            value={formData.phone}
            onChange={handleChange}
            placeholder="010-1234-5678"
          />
          {errors.phone && <InputWarning message={errors.phone} />}
        </div>
        {/* 주소 */}
        <div>
          <label htmlFor="address">주소</label>
          <input
            id="address"
            name="address"
            value={formData.address}
            readOnly
            onClick={() => setAddressModalOpen(true)}
            placeholder="주소 검색"
          />
          {errors.address && <InputWarning message={errors.address} />}
        </div>
        {/* 생년월일 */}
        <div>
          <label htmlFor="birthdate">생년월일</label>
          <input
            id="birthdate"
            name="birthdate"
            type="date"
            value={formData.birthdate}
            onChange={handleChange}
          />
          {errors.birthdate && <InputWarning message={errors.birthdate} />}
        </div>
        {/* 성별 */}
        <div>
          <span>성별</span>
          <label>
            <input
              type="radio"
              name="gender"
              value="남"
              checked={formData.gender === "남"}
              onChange={handleChange}
            /> 남
          </label>
          <label>
            <input
              type="radio"
              name="gender"
              value="여"
              checked={formData.gender === "여"}
              onChange={handleChange}
            /> 여
          </label>
          {errors.gender && <InputWarning message={errors.gender} />}
        </div>
        {/* 관심 장르 선택 */}
        {mode === "register" && (
  <div>
    <label>관심 장르</label>
    <GenreSelect
      selectedGenres={formData.genres}
      onChange={handleGenreChange}
      maxSelect={5}
    />
    {errors.genres && <InputWarning message={errors.genres} />}
  </div>
)}
        <button type="submit">{mode === "register" ? "가입하기" : "수정하기"}</button>
      </form>
      <AddressModal
        isOpen={isAddressModalOpen}
        onClose={() => setAddressModalOpen(false)}
        onSelectAddress={addr => {
          setFormData(prev => ({ ...prev, address: addr }));
          setAddressModalOpen(false);
        }}
      />
      {/* 성공/실패 모달 */}
      {modal.isOpen && (
        <InfoModal
          isOpen={modal.isOpen}
          type={modal.isSuccess ? "success" : "error"}
          title={modal.isSuccess ? (mode === "register" ? "회원가입 완료" : "수정 완료") : (mode === "register" ? "회원가입 실패" : "수정 실패")}
          message={modal.message}
          confirmLabel={modal.isSuccess ? (mode === "register" ? "로그인으로" : "확인") : "확인"}
          cancelLabel={modal.isSuccess && mode === "register" ? "메인으로" : "닫기"}
          onConfirm={modal.isSuccess && mode === "register" ? handleGoToLogin : handleModalClose}
          onCancel={modal.isSuccess && mode === "register" ? handleGoToMain : handleModalClose}
        />
      )}
    </div>
  );
}
