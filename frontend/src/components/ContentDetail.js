import ReportButton from "./common/ReportButton";
import './ContentDetail.css'; // 스타일 따로 분리되어 있다면 import

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
  return (
    <div className="content-detail-container">
      <img className="poster" src={posterUrl} alt={title} />

      <div className="info-container">
        <h1 className="title">{title}</h1>

        <div className="detail-item"><span className="label">개봉일: </span>{releaseDate}</div>
        <div className="detail-item"><span className="label">장르: </span>{genres.join(', ')}</div>
        <div className="detail-item">
          <span className="label">평점: </span>
          <span style={{ color: '#7c3aed', fontSize: '16px' }}>★</span> {rating.toFixed(1)}
        </div>

        <p className="description">{description}</p>

        {/* 신고 버튼을 info 영역 오른쪽 아래에 위치시키기 위한 wrapper */}
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
