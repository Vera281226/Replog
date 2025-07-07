// src/contents/components/ContentsCard.js

import React from 'react';
import { Link } from 'react-router-dom';

/**
 * ContentsCard 컴포넌트
 * ------------------------------------------------------------------------
 * ○ 콘텐츠 정보를 카드 형태로 렌더링
 * ○ 포스터 + 플랫폼 로고(우상단) + 제목 + 출시연도 + 플랫폼명 + 평점 포함
 * ○ 누락된 값은 "관리자에게 문의해주세요"로 표시됨
 * ------------------------------------------------------------------------
 */
function ContentsCard({ content }) {
    // ------------------------------------------------------------------------
    // 콘텐츠 응답 데이터 구조 분해
    // - platforms: 제공 플랫폼 이름 배열 (예: ["Netflix"])
    // ------------------------------------------------------------------------
    const {
        contentId,
        title,
        posterPath,
        releaseDate,
        rating,
        platforms = []
    } = content;

    // ------------------------------------------------------------------------
    // TMDB 포스터 이미지 URL 조립
    // - 없을 경우 기본 이미지로 대체
    // ------------------------------------------------------------------------
    const posterUrl = posterPath
        ? `https://image.tmdb.org/t/p/w500${posterPath}`
        : '/assets/default-poster.png';

    // ------------------------------------------------------------------------
    // 플랫폼명 → 로고 이미지 경로 매핑
    // - 필요한 경우 여기서 로고 추가 가능
    // ------------------------------------------------------------------------
    const platformLogoMap = {
        'Netflix': '/assets/logos/netflix.png',
        'Disney Plus': '/assets/logos/disneyplus.png'
    };

    // ------------------------------------------------------------------------
    // 출시 연도 추출 (releaseDate → YYYY)
    // ------------------------------------------------------------------------
    const releaseYear = releaseDate ? releaseDate.substring(0, 4) : '관리자에게 문의해주세요';

    // ------------------------------------------------------------------------
    // 첫 번째 플랫폼 (로고 및 텍스트 표기용)
    // ------------------------------------------------------------------------
    const firstPlatform = platforms.length > 0 ? platforms[0] : null;

    // ------------------------------------------------------------------------
    // 평점 표시 포맷
    // ------------------------------------------------------------------------
    const ratingDisplay = rating != null ? `평균 ⭐ ${rating.toFixed(1)}` : '관리자에게 문의해주세요';

    return (
        <Link
            to={`/contents/${contentId}/reviews`}
            style={{ textDecoration: 'none', color: 'inherit' }}
        >
            <div className="contents-card">
                <div className="poster-wrapper">
                    <img className="poster-image" src={posterUrl} alt={title || '포스터'} />

                    {/* ✅ 플랫폼 로고 (오른쪽 위 오버레이) */}
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
                    {/* ✅ 콘텐츠 제목 */}
                    <div className="movie-title">{title || '관리자에게 문의해주세요'}</div>

                    {/* ✅ 출시 연도 및 플랫폼명 */}
                    <div className="movie-sub">
                        {releaseYear} {firstPlatform ? `• ${firstPlatform}` : ''}
                    </div>

                    {/* ✅ TMDB 평점 */}
                    <div className="movie-rating">
                        {ratingDisplay}
                    </div>
                </div>
            </div>
        </Link>
    );
}

export default ContentsCard;
