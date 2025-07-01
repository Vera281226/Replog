// src/index/components/TrailerCard.js

import React from 'react';
import '../css/TrailerCard.css'; // ✅ 전용 스타일 분리

/**
 * ✅ TrailerCard 컴포넌트
 * - 유튜브 예고편 썸네일 위에 제목 + 설명 오버레이
 * - 클릭 시 유튜브 영상 새창으로 이동
 *
 * props:
 *  - title: 영상 제목
 *  - description: 영상 설명 (줄거리 등)
 *  - videoKey: 유튜브 영상 ID
 */
const TrailerCard = ({ title, description, videoKey }) => {
  // ✅ 썸네일 및 영상 URL
  const thumbnailUrl = `https://img.youtube.com/vi/${videoKey}/0.jpg`;
  const videoUrl = `https://www.youtube.com/watch?v=${videoKey}`;

  return (
    <a
      href={videoUrl}
      target="_blank"
      rel="noopener noreferrer"
      className="trailer-card"
    >
      {/* ✅ 유튜브 썸네일 이미지 */}
      <img src={thumbnailUrl} alt={title} className="trailer-thumbnail" />

      {/* ✅ 제목/설명 오버레이 */}
      <div className="trailer-overlay">
        <h3 className="trailer-title">{title}</h3>
        <p className="trailer-description">{description}</p>
      </div>
    </a>
  );
};

export default TrailerCard;
