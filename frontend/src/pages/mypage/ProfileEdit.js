// src/pages/mypage/ProfileEdit.js
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../error/api/interceptor';
import GenreSelect from '../../components/member/GenreSelect';
import './ProfileEdit.css';
import InfoModal from '../../components/InfoModal';

export default function ProfileEdit() {
  const nav = useNavigate();
  const fileRef = useRef(null);

  const [info, setInfo] = useState({
    nickname     : '',
    introduction : '',
    genres       : [],     // [{value,label}]
    profileImage : ''
  });
  const [dupMsg,   setDupMsg]   = useState('');
  const [charLeft, setCharLeft] = useState(100);
  const [msg,      setMsg]      = useState('');
  const [loading,  setLoading]  = useState(true);
  const [modalOpen, setModalOpen] = useState(false);

  // 프로필 로드
  useEffect(() => {
    axios.get('/member/profile', { withCredentials:true })
      .then(r => {
        setInfo({
          nickname: r.data.nickname,
          introduction: r.data.introduction || '',
          genres: (r.data.genres || []).map(id => ({ value:id, label:id })),
          profileImage: r.data.profileImage
        });
      })
      .catch(() => setMsg('프로필 정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, []);

  // 닉네임 중복 확인
  const checkDup = async () => {
    if (!info.nickname.trim()) { setDupMsg('닉네임을 입력하세요'); return; }
    try {
      await axios.get('/member/nickname-check', { params:{nickname: info.nickname} });
      setDupMsg('사용 가능한 닉네임입니다.');
    } catch {
      setDupMsg('이미 사용중인 닉네임입니다.');
    }
  };

  // 이미지 미리보기
  const onImageChange = e => {
    const file = e.target.files[0];
    if (file) setInfo(prev => ({ ...prev, profileImage: URL.createObjectURL(file) }));
  };

  // 소개글 입력
  const onIntroChange = e => {
    const v = e.target.value.slice(0,100);
    setInfo(prev => ({ ...prev, introduction:v }));
    setCharLeft(100 - v.length);
  };

  // 저장
  const handleSave = async () => {
    const form = new FormData();
    form.append('nickname',     info.nickname);
    form.append('introduction', info.introduction);
    info.genres.forEach(g => form.append('genres', g.value));
    if (fileRef.current.files[0])
      form.append('image', fileRef.current.files[0]);

    try {
      await axios.post('/member/profile', form, {
        withCredentials:true,
        headers:{'Content-Type':'multipart/form-data'}
      });
      setModalOpen(true);
    } catch (e) {
      const m = e.response?.data?.message || e.message || '수정 실패';
      setMsg(m);
    }
  };

  if (loading) return <p className="profile-edit">로딩 중…</p>;

  // 프로필 이미지 경로 처리
  const getProfileImage = () => {
    // 서버에서 경로가 아예 없거나 빈 문자열일 때 기본 이미지로 대체
    if (!info.profileImage || info.profileImage.trim() === '') {
      return '/uploads/profile/default.jpg';
    }
    return info.profileImage;
  };

  return (
    <section className="profile-edit">
      <h1>프로필 수정</h1>

      {/* 사진 */}
      <div className="img-box">
        <img
          src={getProfileImage()}
          alt="미리보기"
          className="preview"
        />
        <input
          type="file"
          accept="image/*"
          ref={fileRef}
          onChange={onImageChange}
          id="file"
          hidden
        />
        <label htmlFor="file" className="btn profile-edit-btn">
          ✏️ 프로필 사진 수정
        </label>
      </div>

      {/* 닉네임 */}
      <div className="field">
        <label>닉네임</label>
        <input value={info.nickname}
               onChange={e=>setInfo(p=>({...p,nickname:e.target.value}))}/>
        <button onClick={checkDup} className="btn small">중복확인</button>
        {dupMsg && <p className="dup-msg">{dupMsg}</p>}
      </div>

      {/* 소개 */}
      <div className="field">
        <label>소개</label>
        <textarea value={info.introduction} onChange={onIntroChange}/>
        <p className="count">{charLeft} / 100</p>
      </div>

      {/* 장르 */}
      <div className="field">
        <label>관심 장르</label>
        <GenreSelect
          selectedGenres={info.genres}
          onChange={g => setInfo(p=>({...p,genres:g}))}
        />
      </div>

      {msg && <p className="err-msg">{msg}</p>}

      <div className="btn-area">
        <button onClick={handleSave} className="btn primary">저장</button>
        <button onClick={()=>nav(-1)} className="btn cancel">취소</button>
      </div>

      <InfoModal
        isOpen={modalOpen}
        type="success"
        title="프로필 수정 완료"
        message="프로필이 성공적으로 수정되었습니다."
        confirmLabel="메인으로"
        cancelLabel="마이페이지로"
        onConfirm={() => nav('/', { replace:true })}
        onCancel={() => nav('/mypage', { replace:true })}
      />
    </section>
  );
}
