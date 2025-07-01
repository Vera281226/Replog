import React, { useState, useEffect } from 'react';

/**
 * ContentsFilterBox 컴포넌트
 * - 콘텐츠 필터 영역
 * - 정렬, 개봉일, 장르, 연령, 평점, 러닝타임, 플랫폼 필터 제공
 * - '검색하기' 클릭 시 부모로 필터 상태 전달
 */
function ContentsFilterBox({ onFilterChange }) {
    // ✅ 폼 상태 초기값 정의
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

    // ✅ 장르 목록 불러오기
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

    // ✅ 단일 입력 필드 처리
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormState((prev) => ({ ...prev, [name]: value }));
    };

    // ✅ 배열 선택 항목 토글 처리 (장르, 플랫폼 등)
    const handleToggle = (key, value) => {
        setFormState((prev) => ({
            ...prev,
            [key]: prev[key].includes(value)
                ? prev[key].filter((v) => v !== value)
                : [...prev[key], value]
        }));
    };

    // ✅ 검색 버튼 클릭 시 필터 전달
    const handleSubmit = () => {
        onFilterChange(formState);
    };

    // ✅ 필터 초기화 버튼 클릭
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
                    <label>
                        <input type="radio" name="sort" value="popularity"
                               checked={formState.sort === 'popularity'} onChange={handleChange} />
                        인기순
                    </label><br />
                    <label>
                        <input type="radio" name="sort" value="oldest"
                               checked={formState.sort === 'oldest'} onChange={handleChange} />
                        오래된순
                    </label><br />
                    <label>
                        <input type="radio" name="sort" value="latest"
                               checked={formState.sort === 'latest'} onChange={handleChange} />
                        최신순
                    </label>
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
                <label>
                    <input
                        type="radio"
                        name="age"
                        value=""
                        checked={formState.age === ''}
                        onChange={handleChange}
                    />
                    전체
                </label>
                {['12', '15', '19'].map((age) => (
                    <label key={age}>
                        <input
                            type="radio"
                            name="age"
                            value={age}
                            checked={formState.age === age}
                            onChange={handleChange}
                        />
                        {age}
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
                <label>러닝타임</label>
                <input
                    type="number"
                    name="runtimeMin"
                    value={formState.runtimeMin}
                    onChange={handleChange}
                    placeholder="최소 분"
                />
                <input
                    type="number"
                    name="runtimeMax"
                    value={formState.runtimeMax}
                    onChange={handleChange}
                    placeholder="최대 분"
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
