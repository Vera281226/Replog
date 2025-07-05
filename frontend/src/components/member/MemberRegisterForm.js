// src/components/member/MemberRegisterForm.js
import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import InputWarning from "../../error/components/InputWarning";
import AddressModal from "./AddressModal";
import GenreSelect from "./GenreSelect";
import InfoModal from "../InfoModal";
import { validate } from "./validation";
import "./css/MemberRegisterForm.css"; // CSS 경로 확인

const MemberRegisterForm = () => {
  const navigate = useNavigate();
  
  /* ---------- 상태 ---------- */
  const [formData, setFormData] = useState({
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
  });
  const [errors, setErrors] = useState({});
  const [isAddressModalOpen, setAddressModalOpen] = useState(false);
  const [modal, setModal] = useState({
    isOpen: false,
    isSuccess: false,
    message: ""
  });

  /* ---------- 일반 입력 ---------- */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: validate[name](value) }));
    }
  };

  /* ---------- 장르 선택 ---------- */
  const handleGenreChange = (selected) => {
    setFormData((prev) => ({ ...prev, genres: selected }));
    if (errors.genres) {
      setErrors((prev) => ({ ...prev, genres: validate.genres(selected) }));
    }
  };

  /* ---------- 모달 닫기 ---------- */
  const handleModalClose = () => {
    setModal({ isOpen: false, isSuccess: false, message: "" });
  };

  /* ---------- 로그인으로 이동 ---------- */
  const handleGoToLogin = async () => {
    handleModalClose();
    await new Promise(resolve => setTimeout(resolve, 100)); // 비동기 처리
    navigate("/login");
  };

  /* ---------- 메인으로 이동 ---------- */
  const handleGoToMain = async () => {
    handleModalClose();
    await new Promise(resolve => setTimeout(resolve, 100)); // 비동기 처리
    navigate("/");
  };

  /* ---------- 제출 ---------- */
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
      const res = await axios.post("/api/member/signup", payload, {
        withCredentials: true,
      });
      // 회원가입 성공
      setModal({
        isOpen: true,
        isSuccess: true,
        message: "회원가입이 완료되었습니다!"
      });
    } catch (err) {
      // 회원가입 실패
      const errorMessage = typeof err.response?.data === "object" 
        ? err.response.data.message || JSON.stringify(err.response.data)
        : err.response?.data || "회원가입에 실패했습니다.";
      
      setModal({
        isOpen: true,
        isSuccess: false,
        message: errorMessage
      });
    }
  };

  return (
    <div className="member-register-form">
      <h2>회원가입</h2>

      <form onSubmit={handleSubmit}>
        {/* 아이디 */}
        <div>
          <label htmlFor="memberId">아이디</label>
          <input
            id="memberId"
            name="memberId"
            value={formData.memberId}
            onChange={handleChange}
            placeholder="아이디"
          />
          {errors.memberId && <InputWarning message={errors.memberId} />}
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
          <input
            id="nickname"
            name="nickname"
            value={formData.nickname}
            onChange={handleChange}
            placeholder="닉네임"
          />
          {errors.nickname && <InputWarning message={errors.nickname} />}
        </div>

        {/* 이메일 */}
        <div>
          <label htmlFor="email">이메일</label>
          <input
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            placeholder="example@mail.com"
          />
          {errors.email && <InputWarning message={errors.email} />}
        </div>

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
            />{" "}
            남
          </label>
          <label>
            <input
              type="radio"
              name="gender"
              value="여"
              checked={formData.gender === "여"}
              onChange={handleChange}
            />{" "}
            여
          </label>
          {errors.gender && <InputWarning message={errors.gender} />}
        </div>

        {/* 관심 장르 선택 */}
        <div>
          <label>관심 장르</label>
          <GenreSelect
            selectedGenres={formData.genres}
            onChange={handleGenreChange}
            maxSelect={5}
          />
          {errors.genres && <InputWarning message={errors.genres} />}
        </div>

        <button type="submit">가입하기</button>
      </form>

      <AddressModal
  isOpen={isAddressModalOpen}
  onClose={() => setAddressModalOpen(false)}
  onSelectAddress={addr => {
    setFormData(prev => ({ ...prev, address: addr }));
    setAddressModalOpen(false); // 주소 선택 시 모달 닫기
  }}
/>

      {/* 회원가입 성공 모달 */}
      {modal.isOpen && modal.isSuccess && (
        <InfoModal
          isOpen={modal.isOpen}
          type="success"
          title="회원가입 완료"
          message={modal.message}
          confirmLabel="로그인으로"
          cancelLabel="메인으로"
          onConfirm={handleGoToLogin}
          onCancel={handleGoToMain}
        />
      )}

      {/* 회원가입 실패 모달 */}
      {modal.isOpen && !modal.isSuccess && (
        <InfoModal
          isOpen={modal.isOpen}
          type="error"
          title="회원가입 실패"
          message={modal.message}
          confirmLabel="확인"
          cancelLabel="닫기"
          onConfirm={handleModalClose}
          onCancel={handleModalClose}
        />
      )}
    </div>
  );
};

export default MemberRegisterForm;
