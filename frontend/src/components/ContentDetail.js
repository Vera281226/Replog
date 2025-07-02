// src/components/ContentDetail.jsx

import ReportButton from "./common/ReportButton";

export default function ContentDetail({
  title,
  releaseDate,
  genres = [],         // 기본값 빈 배열
  cast = [],           // 기본값 빈 배열
  rating = 0,
  description,
  posterUrl,
  contentId 
}) {
  return (
    <div className="content-detail">
  <img src={posterUrl} alt={title} />
  <div className="info">
    <h1>{title}</h1><ReportButton
          targetType="CONTENT"
          targetId={String(contentId)}
          buttonStyle="icon"
          buttonText="🚨"
        />
    <div className="detail-item"><span className="label">개봉일:</span>{releaseDate}</div>
    <div className="detail-item"><span className="label">장르:</span>{genres.join(', ')}</div>
    <div className="detail-item"><span className="label">출연:</span>{cast.join(', ')}</div>
    <div className="detail-item"><span className="label">평점:</span>⭐ {rating.toFixed(1)}</div>
    <p className="description">{description}</p>
  </div>
</div>
  );
}
