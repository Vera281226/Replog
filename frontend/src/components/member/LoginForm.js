// src/components/member/LoginForm.js

import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { login, fetchCurrentUser } from '../../error/redux/authSlice';
import './css/LoginForm.css';

const LoginForm = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [formData, setFormData] = useState({ memberId: '', password: '' });
  const [errors, setErrors] = useState({});
  const [showPwd, setShowPwd] = useState(false);
  const [msg, setMsg] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    setErrors(prev => ({ ...prev, [name]: '' }));
    setMsg('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const newErrors = {};
    if (!formData.memberId.trim()) newErrors.memberId = '아이디를 입력해주세요.';
    if (!formData.password.trim()) newErrors.password = '비밀번호를 입력해주세요.';
    if (Object.keys(newErrors).length) {
      setErrors(newErrors);
      return;
    }

    try {
      const resultAction = await dispatch(login(formData));
      if (login.fulfilled.match(resultAction)) {
        await dispatch(fetchCurrentUser());
        navigate('/', { replace: true }); // 메인으로 이동
      } else {
        setMsg(resultAction.payload || '로그인에 실패했습니다.');
      }
    } catch {
      setMsg('서버에 연결할 수 없습니다.');
    }
  };

  return (
    <div className="login-container">
      <form className="login-box" onSubmit={handleSubmit}>
        <h2>로그인</h2>

        <div className="input-group">
          <label htmlFor="memberId">아이디</label>
          <input
            id="memberId"
            name="memberId"
            value={formData.memberId}
            onChange={handleChange}
            placeholder="아이디를 입력하세요"
          />
          {errors.memberId && <span className="error">{errors.memberId}</span>}
        </div>

        <div className="input-group">
          <label htmlFor="password">비밀번호</label>
          <div className="pwd-wrapper">
            <input
              id="password"
              name="password"
              type={showPwd ? 'text' : 'password'}
              value={formData.password}
              onChange={handleChange}
              placeholder="비밀번호를 입력하세요"
            />
            <button 
              type="button" 
              className="toggle-btn" 
              onClick={() => setShowPwd(prev => !prev)}
            >
              {showPwd ? '숨기기' : '보기'}
            </button>
          </div>
          {errors.password && <span className="error">{errors.password}</span>}
        </div>

        {msg && <div className="server-msg">{msg}</div>}

        <button type="submit" className="login-btn">로그인</button>

        <div className="link-group">
          <Link to="/signup">회원가입</Link>
        </div>
      </form>
    </div>
  );
};

export default LoginForm;
