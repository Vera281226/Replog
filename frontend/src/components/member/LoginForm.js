// src/components/member/LoginForm.js
import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
// import './css/LoginForm.css';

const LoginForm = () => {
  const navigate = useNavigate();

  /* 상태 */
  const [formData, setFormData] = useState({ memberId: '', pwd: '' });
  const [errors,   setErrors]   = useState({});
  const [showPwd,  setShowPwd]  = useState(false);
  const [msg,      setMsg]      = useState('');

  /* 입력 변경 */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    setErrors((prev) => ({ ...prev, [name]: '' }));   // 에러 초기화
  };

  /* 로그인 */
  const handleSubmit = async (e) => {
    e.preventDefault();

    const newErrors = {};
    if (!formData.memberId.trim()) newErrors.memberId = '아이디를 입력하세요';
    if (!formData.pwd.trim())      newErrors.pwd      = '비밀번호를 입력하세요';
    if (Object.keys(newErrors).length) { setErrors(newErrors); return; }

    try {
      const { data } = await axios.post('/api/auth/login', formData, { withCredentials: true });
      setMsg(data);              // "로그인 성공"
      navigate('/mypage', { replace: true });
    } catch (err) {
      setMsg(err.response?.data || '로그인 실패');
    }
  };

  return (
    <div className="login-wrapper">
      <h2>로그인</h2>

      <form onSubmit={handleSubmit} className="login-form">
        {/* 아이디 */}
        <div className="field">
          <label htmlFor="memberId">아이디</label>
          <input
            id="memberId"
            name="memberId"
            value={formData.memberId}
            onChange={handleChange}
            autoComplete="username"
          />
          {errors.memberId && <p className="error">{errors.memberId}</p>}
        </div>

        {/* 비밀번호 + 보기/숨김 토글 */}
        <div className="field">
          <label htmlFor="pwd">비밀번호</label>
          <div className="pwd-box">
            <input
              id="pwd"
              name="pwd"
              type={showPwd ? 'text' : 'password'}
              value={formData.pwd}
              onChange={handleChange}
              autoComplete="current-password"
            />
            <button
              type="button"
              className="toggle"
              onClick={() => setShowPwd((prev) => !prev)}
            >
              {showPwd ? '숨김' : '보기'}
            </button>
          </div>
          {errors.pwd && <p className="error">{errors.pwd}</p>}
        </div>

        {/* 서버 메시지 */}
        {msg && <p className="msg">{msg}</p>}

        <button type="submit" className="primary">로그인</button>
      </form>

      <nav className="links">
        <Link to="/signup">회원가입</Link>
      </nav>
    </div>
  );
};

export default LoginForm;
