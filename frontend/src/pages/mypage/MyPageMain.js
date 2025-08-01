import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../../error/api/interceptor';
import './MyPage.css';
import MyReportList from './MyReportList';
import WithdrawModal from './WithdrawModal';
import MyReviewList from './MyReviewList';

export default function MyPageMain() {
  const nav = useNavigate();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState('');
  const [showReports, setShowReports] = useState(false);
  const [showMyReviews, setShowMyReviews] = useState(false);
  const [showWithdraw, setShowWithdraw] = useState(false); // 탈퇴 모달 표시

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
        src={data.profileImage || '/uploads/profile/default.jpg'}
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

      <div className="link-box">
        <button className="link-btn" onClick={() => setShowMyReviews(true)}>
  내가 쓴 리뷰보기
</button>
{showMyReviews && (
  <MyReviewList onClose={() => setShowMyReviews(false)} />
)}
        <button className="link-btn" onClick={() => setShowReports(true)}>
          내가 쓴 요청보기
        </button>
      </div>
      {showReports && <MyReportList onClose={() => setShowReports(false)} />}

      {/* --- 여기서부터 회원 탈퇴 버튼 추가 --- */}
      <div style={{ marginTop: 40, textAlign: 'center' }}>
        <button
          className="btn danger"
          style={{
            background: '#fff',
            color: '#dc3545',
            border: '1px solid #dc3545',
            padding: '10px 20px',
            borderRadius: '4px',
            cursor: 'pointer',
            fontWeight: 'bold'
          }}
          onClick={() => setShowWithdraw(true)}
        >
          회원 탈퇴
        </button>
      </div>
      {showWithdraw && (
        <WithdrawModal
          onClose={() => setShowWithdraw(false)}
          onSuccess={() => {
            setShowWithdraw(false);
            alert('회원 탈퇴가 완료되었습니다.');
            nav('/'); // 탈퇴 후 메인으로 이동
            // 필요하다면 로그아웃 처리도 추가
          }}
        />
      )}
    </section>
  );
}