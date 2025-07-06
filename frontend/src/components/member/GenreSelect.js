// src/components/member/GenreSelect.js
import React, { useEffect, useState, useCallback } from 'react';
import axios from '../../error/api/interceptor';
import './GenreSelect.css';          // 아래 CSS 참고

const GenreSelect = ({ selectedGenres = [], onChange, maxSelect = 5 }) => {
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  /* 장르 목록 로드 */
  useEffect(() => {
    const fetch = async () => {
      try {
        setLoading(true);
        const res = await axios.get('/genres');
        setOptions(res.data.map(g => ({ value: g.genreId, label: g.name })));
      } catch {
        setErrorMsg('장르 목록을 불러오지 못했습니다.');
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, []);

  /* 선택/해제 토글[4] */
  const toggle = useCallback((opt) => {
    const exists = selectedGenres.some(g => g.value === opt.value);
    let next;

    if (exists) {
      next = selectedGenres.filter(g => g.value !== opt.value);
    } else {
      if (selectedGenres.length >= maxSelect) return;   // 선택 제한
      next = [...selectedGenres, opt];
    }
    onChange(next);
  }, [selectedGenres, onChange, maxSelect]);

  if (loading)  return <p>로딩 중…</p>;
  if (errorMsg) return <p>{errorMsg}</p>;

  return (
    <div className="genre-select">
      {options.map(opt => {
        const active = selectedGenres.some(g => g.value === opt.value);
        return (
          <button
            key={opt.value}
            type="button"
            className={`genre-btn ${active ? 'selected' : ''}`}
            onClick={() => toggle(opt)}
            aria-pressed={active}
          >
            {opt.label}
          </button>
        );
      })}
      <p className="counter">
        {selectedGenres.length} / {maxSelect} 선택
      </p>
    </div>
  );
};

export default GenreSelect;
