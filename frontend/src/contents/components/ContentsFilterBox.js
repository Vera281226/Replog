// src/contents/components/ContentsFilterBox.js

import React, { useState, useEffect } from 'react';

/**
 * ContentsFilterBox 컴포넌트
 * -------------------------------------------------------------------
 * ○ 콘텐츠 필터 영역
 * ○ 정렬, 개봉일, 장르, 연령, 평점, 러닝타임, 플랫폼 필터 제공
 * ○ '검색하기' 클릭 시 부모로 필터 상태 전달 (onFilterChange)
 * -------------------------------------------------------------------
 */
function ContentsFilterBox({ onFilterChange }) {
    // -------------------------------------------------------------------
    // ✅ 필터 상태 정의
    // -------------------------------------------------------------------
    const [formState, setFormState] = useState({
        sort: '',
        releaseStart: '',
        releaseEnd: '',
        genres: [],
        age: '',
        ratingMin: '',
        ratingMax: '',
        runtimeMin: '',
        runtimeMax: '',
        platforms: []
    });

    // ✅ 장르 리스트 상태
    const [genreList, setGenreList] = useState([]);

    // -------------------------------------------------------------------
    // ✅ 장르 목록 불러오기 (/api/genres)
    // -------------------------------------------------------------------
    useEffect(() => {
        const fetchGenres = async () => {
            try {
                const res = await fetch('/api/genres');
                const data = await res.json();
                setGenreList(data); // [{ genreId, name }]
            } catch (err) {
                console.error('장르 목록 불러오기 실패:', err);
            }
        };
        fetchGenres();
    }, []);

    // -------------------------------------------------------------------
    // ✅ 일반 입력 필드 변경 처리 (정렬, 날짜, 평점 등)
    // -------------------------------------------------------------------
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormState((prev) => ({ ...prev, [name]: value }));
    };

    // -------------------------------------------------------------------
    // ✅ 체크박스/버튼형 필드 토글 (장르, 플랫폼)
    // -------------------------------------------------------------------
    const handleToggle = (key, value) => {
        setFormState((prev) => ({
            ...prev,
            [key]: prev[key].includes(value)
                ? prev[key].filter((v) => v !== value)
                : [...prev[key], value]
        }));
    };

    // -------------------------------------------------------------------
    // ✅ 검색 버튼 클릭 시 필터 적용
    // -------------------------------------------------------------------
    const handleSubmit = () => {
        onFilterChange(formState);
    };

    // -------------------------------------------------------------------
    // ✅ 필터 초기화
    // -------------------------------------------------------------------
    const handleReset = () => {
        const empty = {
            sort: '',
            releaseStart: '',
            releaseEnd: '',
            genres: [],
            age: '',
            ratingMin: '',
            ratingMax: '',
            runtimeMin: '',
            runtimeMax: '',
            platforms: []
        };
        setFormState(empty);
        onFilterChange({});
    };

    return (
        <div className="filter-box">
            <h3>필터</h3>

            {/* ✅ 정렬 */}
            <div className="filter-section">
                <label>정렬</label>
                <div>
                    {['popularity', 'oldest', 'latest'].map((value) => (
                        <label key={value}>
                            <input
                                type="radio"
                                name="sort"
                                value={value}
                                checked={formState.sort === value}
                                onChange={handleChange}
                            />
                            {value === 'popularity' ? '인기순' : value === 'oldest' ? '오래된순' : '최신순'}
                        </label>
                    ))}
                </div>
            </div>

            {/* ✅ 개봉일 */}
            <div className="filter-section">
                <label>개봉일</label>
                <input type="date" name="releaseStart" value={formState.releaseStart} onChange={handleChange} />
                <input type="date" name="releaseEnd" value={formState.releaseEnd} onChange={handleChange} />
            </div>

            {/* ✅ 장르 */}
            <div className="filter-section">
                <label>장르</label>
                <div className="genre-buttons">
                    {genreList.map((genre) => (
                        <button
                            key={genre.genreId}
                            type="button"
                            onClick={() => handleToggle('genres', genre.name)}
                            className={formState.genres.includes(genre.name) ? 'selected' : ''}
                        >
                            {genre.name}
                        </button>
                    ))}
                </div>
            </div>

            {/* ✅ 시청 연령 */}
            <div className="filter-section">
                <label>시청 연령</label>
                {['', '12', '15', '19'].map((age) => (
                    <label key={age || 'all'}>
                        <input
                            type="radio"
                            name="age"
                            value={age}
                            checked={formState.age === age}
                            onChange={handleChange}
                        />
                        {age === '' ? '전체' : age}
                    </label>
                ))}
            </div>

            {/* ✅ 평점 */}
            <div className="filter-section">
                <label>평점</label>
                <input
                    type="number"
                    name="ratingMin"
                    value={formState.ratingMin}
                    onChange={handleChange}
                    placeholder="최소 평점"
                />
                <input
                    type="number"
                    name="ratingMax"
                    value={formState.ratingMax}
                    onChange={handleChange}
                    placeholder="최대 평점"
                />
            </div>

            {/* ✅ 러닝타임 */}
            <div className="filter-section">
                <label>러닝타임 (분)</label>
                <input
                    type="number"
                    name="runtimeMin"
                    value={formState.runtimeMin}
                    onChange={handleChange}
                    placeholder="최소"
                />
                <input
                    type="number"
                    name="runtimeMax"
                    value={formState.runtimeMax}
                    onChange={handleChange}
                    placeholder="최대"
                />
            </div>

            {/* ✅ 플랫폼 */}
            <div className="filter-section">
                <label>플랫폼</label>
                {['Netflix', 'Disney Plus'].map((platform) => (
                    <label key={platform}>
                        <input
                            type="checkbox"
                            checked={formState.platforms.includes(platform)}
                            onChange={() => handleToggle('platforms', platform)}
                        />
                        {platform}
                    </label>
                ))}
            </div>

            {/* ✅ 버튼 영역 */}
            <button className="search-button" onClick={handleSubmit}>
                검색하기
            </button>
            <button className="search-button reset" onClick={handleReset}>
                필터 초기화
            </button>
        </div>
    );
}

export default ContentsFilterBox;
