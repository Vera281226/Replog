// src/pages/mypage/ProfileEdit.js
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../../error/api/interceptor';
import GenreSelect from '../../components/member/GenreSelect';
import './ProfileEdit.css';

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

  /* ① 프로필 로드 */
  useEffect(() => {
    axios.get('/member/profile', { withCredentials:true })
      .then(r => {
  setInfo({
    nickname     : r.data.nickname,
    introduction : r.data.introduction || '',
    genres       : (r.data.genres || []).map(id => ({ value:id, label:id })), // ID→태그 객체
    profileImage : r.data.profileImage        // imagePath → profileImage 로 변경
  });
})
      .catch(() => setMsg('프로필 정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, []);

  /* ② 닉네임 중복 확인 */
  const checkDup = async () => {
    if (!info.nickname.trim()) { setDupMsg('닉네임을 입력하세요'); return; }
    try {
      await axios.get('/member/nickname-check', { params:{n: info.nickname} });
      setDupMsg('사용 가능한 닉네임입니다.');
    } catch {
      setDupMsg('이미 사용중인 닉네임입니다.');
    }
  };

  /* ③ 이미지 미리보기 */
  const onImageChange = e => {
    const file = e.target.files[0];
    if (file) setInfo(prev => ({ ...prev, profileImage: URL.createObjectURL(file) }));
  };

  /* ④ 소개글 입력 */
  const onIntroChange = e => {
    const v = e.target.value.slice(0,100);
    setInfo(prev => ({ ...prev, introduction:v }));
    setCharLeft(100 - v.length);
  };

  /* ⑤ 저장 */
  const handleSave = async () => {
    const form = new FormData();
    form.append('nickname',     info.nickname);
    form.append('introduction', info.introduction);

    /* genres=14&genres=37… 형태로 전송 */
    info.genres.forEach(g => form.append('genres', g.value));

    if (fileRef.current.files[0])
      form.append('image', fileRef.current.files[0]);

    try {
      await axios.post('/member/profile', form, {
        withCredentials:true,
        headers:{'Content-Type':'multipart/form-data'}
      });
      alert('프로필이 수정되었습니다.');
      nav('/mypage', { replace:true });
    } catch (e) {
      const m = e.response?.data?.message || e.message || '수정 실패';
      setMsg(m);
    }
  };

  if (loading) return <p className="profile-edit">로딩 중…</p>;

  return (
    <section className="profile-edit">
      <h1>프로필 수정</h1>

      {/* 사진 */}
      <div className="img-box">
        <img src={info.profileImage || '/img/default-profile.svg'} alt="미리보기" className="preview"/>
        <input type="file" accept="image/*" ref={fileRef} onChange={onImageChange} id="file" hidden/>
        <label htmlFor="file" className="btn small">📷</label>
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
        <button onClick={()=>nav(-1)} className="btn">취소</button>
      </div>
    </section>
  );
}
