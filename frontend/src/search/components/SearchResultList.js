// src/search/components/SearchResultList.js

import React from 'react';

/**
 * SearchResultList 컴포넌트
 * - 검색 결과 중 영화(movie) / 시리즈(tv)로 나누어 리스트 출력
 * - 포스터, 제목, 평점, 개요 등 표시
 */
function SearchResultList({ results }) {
    const movies = results.filter(item => item.mediaType === 'movie');
    const tvSeries = results.filter(item => item.mediaType === 'tv');

    const renderList = (list) => (
        <div className="content-list">
            {list.map(item => (
                <div key={item.contentId} className="content-card">
                    <img
                        src={`https://image.tmdb.org/t/p/w200${item.posterPath}`}
                        alt={item.title}
                        className="content-poster"
                    />
                    <div className="content-info">
                        <h4 className="content-title">{item.title}</h4>
                        <p className="content-sub">
                            {item.releaseDate?.slice(0, 4)} · {item.rating}점
                        </p>
                        <p className="content-overview">{item.overview}</p>
                    </div>
                </div>
            ))}
        </div>
    );

    return (
        <div className="result-list-section">
            {movies.length > 0 && (
                <>
                    <h3 className="section-title">🎬 영화</h3>
                    {renderList(movies)}
                </>
            )}
            {tvSeries.length > 0 && (
                <>
                    <h3 className="section-title">📺 시리즈</h3>
                    {renderList(tvSeries)}
                </>
            )}
        </div>
    );
}

export default SearchResultList;
