// src/components/member/MemberRegisterForm.js
import React, { useState } from 'react';
import axios from 'axios';
import InputWarning from '../../error/components/InputWarning';
import AddressModal from './AddressModal';
import GenreSelect from './GenreSelect';
import { validate } from './validation';
import './css/MemberRegisterForm.css';  // CSS 경로 확인

const MemberRegisterForm = () => {
  /* ---------- 상태 ---------- */
  const [formData, setFormData] = useState({
    id: '', password: '', name: '', nickname: '',
    email: '', phone: '', address: '', birthdate: '',
    gender: '', genres: []
  });
  const [errors, setErrors] = useState({});
  const [msg, setMsg] = useState('');
  const [isAddressModalOpen, setAddressModalOpen] = useState(false);

  /* ---------- 일반 입력 ---------- */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: validate[name](value) }));
    }
  };

  /* ---------- 장르 선택 ---------- */
  const handleGenreChange = (selected) => {
    setFormData(prev => ({ ...prev, genres: selected }));
    if (errors.genres) {
      setErrors(prev => ({ ...prev, genres: validate.genres(selected) }));
    }
  };

  /* ---------- 제출 ---------- */
  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = {};
    Object.keys(formData).forEach(key => {
      newErrors[key] =
        key === 'genres'
          ? validate.genres(formData.genres)
          : validate[key](formData[key]);
    });
    setErrors(newErrors);
    if (Object.values(newErrors).some(v => v)) return;

    const payload = {
      ...formData,
      genres: formData.genres.map(g => g.value)
    };

    try {
      const res = await axios.post('/api/member/signup', payload, { withCredentials: true });
      setMsg(res.data);
    } catch (err) {
      setMsg(err.response?.data || '회원가입 실패');
    }
  };

  return (
    <div className="member-register-form">
      <h2>회원가입</h2>
      {msg && <p className="msg">{msg}</p>}

      <form onSubmit={handleSubmit}>
        {/* 아이디 */}
        <div>
          <label htmlFor="id">아이디</label>
          <input
            id="id" name="id" value={formData.id}
            onChange={handleChange} placeholder="아이디"
          />
          {errors.id && <InputWarning message={errors.id} />}
        </div>

        {/* 비밀번호 */}
        <div>
          <label htmlFor="password">비밀번호</label>
          <input
            id="password" type="password" name="password"
            value={formData.password} onChange={handleChange}
            placeholder="비밀번호"
          />
          {errors.password && <InputWarning message={errors.password} />}
        </div>

        {/* 이름 */}
        <div>
          <label htmlFor="name">이름</label>
          <input
            id="name" name="name" value={formData.name}
            onChange={handleChange} placeholder="이름"
          />
          {errors.name && <InputWarning message={errors.name} />}
        </div>

        {/* 닉네임 */}
        <div>
          <label htmlFor="nickname">닉네임</label>
          <input
            id="nickname" name="nickname" value={formData.nickname}
            onChange={handleChange} placeholder="닉네임"
          />
          {errors.nickname && <InputWarning message={errors.nickname} />}
        </div>

        {/* 이메일 */}
        <div>
          <label htmlFor="email">이메일</label>
          <input
            id="email" name="email" value={formData.email}
            onChange={handleChange} placeholder="example@mail.com"
          />
          {errors.email && <InputWarning message={errors.email} />}
        </div>

        {/* 휴대폰 */}
        <div>
          <label htmlFor="phone">휴대폰</label>
          <input
            id="phone" name="phone" value={formData.phone}
            onChange={handleChange} placeholder="010-1234-5678"
          />
          {errors.phone && <InputWarning message={errors.phone} />}
        </div>

        {/* 주소 */}
        <div>
          <label htmlFor="address">주소</label>
          <input
            id="address" name="address" value={formData.address}
            readOnly onClick={() => setAddressModalOpen(true)}
            placeholder="주소 검색"
          />
          {errors.address && <InputWarning message={errors.address} />}
        </div>

        {/* 생년월일 */}
        <div>
          <label htmlFor="birthdate">생년월일</label>
          <input
            id="birthdate" name="birthdate" type="date"
            value={formData.birthdate} onChange={handleChange}
          />
          {errors.birthdate && <InputWarning message={errors.birthdate} />}
        </div>

        {/* 성별 */}
        <div>
          <span>성별</span>
          <label>
            <input
              type="radio" name="gender" value="남"
              checked={formData.gender === '남'} onChange={handleChange}
            /> 남
          </label>
          <label>
            <input
              type="radio" name="gender" value="여"
              checked={formData.gender === '여'} onChange={handleChange}
            /> 여
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
        onSelectAddress={(addr) =>
          setFormData(prev => ({ ...prev, address: addr }))
        }
      />
    </div>
  );
};

export default MemberRegisterForm;
