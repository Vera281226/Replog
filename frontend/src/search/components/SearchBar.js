import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/SearchPage.css';  // ✅ 경로 수정: css는 바로 위 폴더에 있음

function SearchBar() {
    const [keyword, setKeyword] = useState('');
    const navigate = useNavigate();

    // 검색 실행
    const handleSearch = () => {
        const trimmed = keyword.trim();
        if (trimmed) {
            navigate(`/search?keyword=${encodeURIComponent(trimmed)}`);
        }
    };

    // 엔터 키 처리
    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    return (
        <div className="search-bar-container">
            <input
                type="text"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="검색어를 입력하세요"
                className="search-input"
            />
            <button onClick={handleSearch} className="search-button">
                🔍
            </button>
        </div>
    );
}

export default SearchBar;
