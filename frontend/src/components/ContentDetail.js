// src/components/ContentDetail.jsx

import React from 'react';

function ContentDetail({
  title = "가제: 컨텐츠 제목",
  releaseDate = "2023-01-01",
  genres = ["드라마", "스릴러"],
  cast = ["배우 A", "배우 B"],
  rating = 4.5,
  description = "이 컨텐츠는 ... 줄거리 요약입니다.",
  posterUrl = "https://via.placeholder.com/200x300",
}) {
  return (
    <div className="flex flex-col md:flex-row p-4 border-b">
      {/* 포스터 */}
      <img
        src={posterUrl}
        alt={title}
        className="w-40 h-auto rounded shadow-md mb-4 md:mb-0 md:mr-6"
      />

      {/* 텍스트 정보 */}
      <div className="flex flex-col gap-2 text-gray-800">
        <h1 className="text-2xl font-bold">{title}</h1>
        <div className="text-sm text-gray-500">개봉일: {releaseDate}</div>
        <div>
          <span className="font-semibold">장르:</span> {genres.join(', ')}
        </div>
        <div>
          <span className="font-semibold">출연:</span> {cast.join(', ')}
        </div>
        <div>
          <span className="font-semibold">평점:</span> ⭐ {rating.toFixed(1)}
        </div>
        <p className="mt-2 text-sm">{description}</p>
      </div>
    </div>
  );
}

export default ContentDetail;
