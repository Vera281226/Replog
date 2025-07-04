import React from "react"; 
import { useNavigate } from "react-router-dom";

export default function PostContent({
  post,
  liked,
  toggleLike,
  onEdit,
  onDelete,
  isAuthor,
  isAuthenticated,
}) {
  const navigate = useNavigate();

  const formatFullDateTime = (dateStr) => {
    const date = new Date(dateStr);
    const year = String(date.getFullYear()).slice(2);
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    return `${year}.${month}.${day} ${hours}:${minutes}`;
  };

  const isEdited = post.updatedAt && post.updatedAt !== post.createdAt;
  const displayDate = isEdited ? post.updatedAt : post.createdAt;

  return (
    <div className="post-detail-box">
      <h2 className="post-title">{post.title}</h2>

      <div className="post-content">
        <div className="post-stats">
          조회수 {post.views} | 추천수 {post.likes}
        </div>

        <div className="post-body" dangerouslySetInnerHTML={{ __html: post.content }} />

        <div className="post-recommend-wrapper">
          <button
            onClick={toggleLike}
            disabled={!isAuthenticated}
            className={`post-recommend-button ${
              liked
                ? "liked"
                : isAuthenticated
                ? "default"
                : "disabled"
            }`}
          >
            이 게시물 추천
          </button>
        </div>

        <div className="post-author-info">
          작성자: {post.nickname} <br />
          {formatFullDateTime(displayDate)}{isEdited && " (수정됨)"}
        </div>
      </div>

      <div className="post-action-buttons">
        <button className="post-back-button" onClick={() => navigate("/boards")}>
          목록
        </button>

        {isAuthor && (
          <div className="post-author-actions">
            <button onClick={onEdit} className="post-edit-button">
              수정
            </button>
            <button onClick={onDelete} className="post-delete-button">
              삭제
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
