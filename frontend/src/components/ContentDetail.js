import { useEffect, useState } from 'react';
import ReportButton from "./common/ReportButton";
import api from '../error/api/interceptor';
import './ContentDetail.css';

export default function ContentDetail({
  title,
  releaseDate,
  genres = [],
  cast = [],
  rating = 0,
  description,
  posterUrl,
  contentId
}) {
  const [userReviewRating, setUserReviewRating] = useState(null);

  const fetchAverageRating = async () => {
    try {
      const res = await api.get('/reviews/average', {
        params: { contentId }
      });
      setUserReviewRating(res.data);
    } catch (err) {
      console.error('평점 불러오기 실패:', err);
      setUserReviewRating(null);
    }
  };

  useEffect(() => {
    fetchAverageRating();
  }, [contentId]);

  return (
    <div className="content-detail-container">
      <img className="poster" src={posterUrl} alt={title} />

      <div className="info-container">
        <h1 className="title">{title}</h1>

        <div className="detail-item">
          <span className="label">개봉일: </span>{releaseDate}
        </div>

        <div className="detail-item">
          <span className="label">장르: </span>{genres.join(', ')}
        </div>

        <div className="detail-item">
          <span className="label">평점: </span>
          <span style={{ color: '#ff8c00' }}>★</span> {(rating / 2).toFixed(1)} / 5.0
          <span style={{ color: '#888', marginLeft: '6px' }}>(TMDB 기준)</span>
        </div>

        <div className="detail-item">
          <span className="label">리뷰 평점: </span>
          <span style={{ color: '#7c3aed' }}>★</span>{' '}
          {userReviewRating !== null ? `${userReviewRating.toFixed(1)} / 5.0` : '평점 없음'}
          <span style={{ color: '#888', marginLeft: '6px' }}>(Replog 기준)</span>
        </div>

        <p className="description">{description}</p>

        <div className="report-btn-wrapper">
          <ReportButton
            targetType="CONTENT"
            targetId={String(contentId)}
            buttonText="🚨신고하기"
          />
        </div>
      </div>
    </div>
  );
}
