// src/pages/mypage/MyPageMain.js

import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../../error/api/interceptor';
import './MyPage.css';

export default function MyPageMain() {
  const nav = useNavigate();
  const [data, setData] = useState(null);        // 서버 데이터
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState('');

  useEffect(() => {
    api.get('/member/mypage', { withCredentials: true })
      .then(r => setData(r.data))
      .catch(e => {
        if (e.response?.status === 401) nav('/login');
        else setErr('마이페이지 정보를 불러오지 못했습니다.');
      })
      .finally(() => setLoading(false));
  }, [nav]);

  if (loading) return <p className="mypage">로딩 중…</p>;
  if (err)     return <p className="mypage error">{err}</p>;
  if (!data)   return <p className="mypage error">마이페이지 데이터가 없습니다.</p>;

  return (
    <section className="mypage">
      {/* ① 프로필 영역 */}
      <img
        src={data.profileImage || '/img/default-profile.svg'}
        alt="프로필"
        className="profile-img"
      />
      <h2>{data.nickname || '닉네임 없음'}</h2>
      <p className="intro">{data.introduction || '소개글이 없습니다.'}</p>
      <p className="count">
        게시글 {data.reviewCount ?? 0} | 댓글 {data.commentCount ?? 0}
      </p>

      {/* ② 프로필 수정 버튼 */}
      <Link to="/mypage/edit" className="btn edit">프로필 수정</Link>

      {/* ②-1. 회원 정보 수정 버튼 추가 */}
      <Link to="/member/edit" className="btn info">회원 정보 수정</Link>


      {/* ③ 기타 메뉴 (원하면 링크 추가) */}
      <div className="link-box">
        <Link to="#">내가 쓴 리뷰보기</Link>
        <Link to="#">내가 쓴 모집글 · 신청내역</Link>
        <Link to="#">내가 쓴 요청보기</Link>
      </div>
    </section>
  );
}
