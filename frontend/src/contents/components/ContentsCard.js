import React from 'react';

/**
 * ContentsCard 컴포넌트
 * - 콘텐츠 정보를 카드 형태로 렌더링
 * - 포스터 + 플랫폼 로고 + 제목 + 개봉일 + 매체 + 평점 + 플랫폼명 포함
 */
function ContentsCard({ content }) {
    const {
        title,
        posterPath,
        mediaType,
        releaseDate,
        rating,
        platform // ✅ 배열 (예: ['Netflix', 'Disney Plus'])
    } = content;

    const baseImageUrl = 'https://image.tmdb.org/t/p/w500';
    const posterUrl = posterPath
        ? `${baseImageUrl}${posterPath}`
        : '/assets/default-poster.png';

    // ✅ 플랫폼 로고 매핑
    const platformLogoMap = {
        'Netflix': '/assets/logos/netflix.png',
        'Disney Plus': '/assets/logos/disneyplus.png'
    };

    // ✅ 매체 유형 한글 변환
    const mediaTypeKorean = mediaType === 'movie' ? '영화' : 'TV 시리즈';

    return (
        <div className="contents-card">
            {/* ✅ 포스터 + 플랫폼 로고 오버레이 */}
            <div className="poster-wrapper">
                <img className="poster-image" src={posterUrl} alt={title} />

                {/* ✅ 여러 플랫폼 로고 모두 출력 */}
                <div className="logo-overlay-multi">
                    {platform?.map((pf) => (
                        platformLogoMap[pf] && (
                            <img
                                key={pf}
                                className="logo-icon"
                                src={platformLogoMap[pf]}
                                alt={pf}
                                title={pf}
                            />
                        )
                    ))}
                </div>
            </div>

            {/* ✅ 카드 하단 정보 */}
            <div className="movie-info">
                <div className="movie-title">{title}</div>
                <div className="movie-sub">{`${mediaTypeKorean} | ${releaseDate}`}</div>
                <div className="movie-rating">
                    {rating ? `⭐ ${rating.toFixed(1)}` : '⭐ 평점 없음'}
                </div>
                <div className="movie-platform">
                    {platform?.join(', ') || '플랫폼 정보 없음'}
                </div>
            </div>
        </div>
    );
}

export default ContentsCard;
