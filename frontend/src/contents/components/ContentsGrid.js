// src/contents/components/ContentsGrid.js

import React from 'react';
import ContentsCard from './ContentsCard';

/**
 * ContentsGrid 컴포넌트
 * ------------------------------------------------------------------
 * ○ 전달받은 콘텐츠 배열을 카드 형태로 출력
 * ○ 각 콘텐츠는 ContentsCard 컴포넌트를 통해 렌더링됨
 * ○ 스타일은 contents-page 전용 `.contents-grid` 클래스를 따름
 * ------------------------------------------------------------------
 */
function ContentsGrid({ contents }) {
    return (
        <div className="contents-grid">
            {contents.map((item) => (
                <ContentsCard
                    key={item.contentId}
                    content={{
                        contentId: item.contentId,
                        title: item.title,
                        posterPath: item.posterPath,
                        mediaType: item.mediaType,
                        releaseDate: item.releaseDate,
                        rating: item.rating,
                        platforms: item.platforms // ✅ 수정된 필드명
                    }}
                />
            ))}
        </div>
    );
}

export default ContentsGrid;
