// src/contents/components/ContentsCard.js

import React from 'react';
import { Link } from 'react-router-dom';

function ContentsCard({ content }) {
    const {
        contentId,
        title,
        posterPath,
        releaseDate,
        rating,
        platforms = []
    } = content;

    const posterUrl = posterPath
        ? `https://image.tmdb.org/t/p/w500${posterPath}`
        : '/assets/default-poster.png';

    const platformLogoMap = {
        'Netflix': '/images/netflix-logo.png',
        'Disney Plus': '/images/disney-logo.png'
    };

    const firstPlatform = platforms.length > 0 ? platforms[0] : null;
    const releaseYear = releaseDate ? releaseDate.substring(0, 4) : '관리자에게 문의해주세요';
    const ratingDisplay = rating != null ? `평균 ⭐ ${rating.toFixed(1)}` : '관리자에게 문의해주세요';

    return (
        <Link
            to={`/contents/${contentId}/reviews`}
            style={{ textDecoration: 'none', color: 'inherit' }}
        >
            <div className="contents-card">
                <div className="contents-poster-wrapper">
                    <img className="contents-poster-image" src={posterUrl} alt={title || '포스터'} />

                    {firstPlatform && platformLogoMap[firstPlatform] && (
                        <img
                            className="platform-logo-overlay"
                            src={platformLogoMap[firstPlatform]}
                            alt={firstPlatform}
                            title={firstPlatform}
                        />
                    )}
                </div>

                <div className="movie-info">
                    <div className="movie-title">{title || '관리자에게 문의해주세요'}</div>
                    <div className="movie-sub">
                        {releaseYear} {firstPlatform ? `• ${firstPlatform}` : ''}
                    </div>
                    <div className="movie-rating">{ratingDisplay}</div>
                </div>
            </div>
        </Link>
    );
}

export default ContentsCard;
