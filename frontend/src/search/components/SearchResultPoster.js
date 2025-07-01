// src/search/components/SearchResultPoster.js

import React from 'react';
import '../../index/css/IndexPage.css'; // ✅ 정확한 경로로 수정 (MovieCard 스타일 재활용)

/**
 * SearchResultPoster 컴포넌트
 * - 검색 결과 중 상위 6개의 콘텐츠를 대표 포스터 형태로 슬라이드 출력
 * - TMDB 이미지와 제목, 개봉연도, 콘텐츠 유형 표시
 */
function SearchResultPoster({ results }) {
    // 🔹 검색 결과 중 앞에서 6개만 추출
    const top6 = results.slice(0, 6);

    return (
        <div className="poster-section">
            {/* 🔸 섹션 제목 */}
            <h3 className="section-title">연관 콘텐츠</h3>

            {/* 🔸 가로 슬라이드 형식 */}
            <div className="poster-slider">
                {top6.map((item) => (
                    <div key={item.contentId} className="poster-card">
                        <img
                            src={`https://image.tmdb.org/t/p/w300${item.posterPath}`}
                            alt={item.title}
                            className="poster-image"
                        />
                        <div className="poster-info">
                            <p className="poster-title">{item.title}</p>
                            <p className="poster-meta">
                                {item.releaseDate?.slice(0, 4)} · {item.mediaType === 'movie' ? '영화' : '시리즈'}
                            </p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default SearchResultPoster;
