import React from "react";
import { useNavigate } from "react-router-dom";

export default function PostContent({ post, userId, liked, toggleLike, onEdit, onDelete }) {
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
    <div style={{ padding: "24px", border: "1px solid #ddd", borderRadius: "8px", backgroundColor: "#fff" }}>
      <h2
        style={{
          backgroundColor: "#eee",
          padding: "16px",
          borderRadius: "6px",
          fontSize: "1.5rem",
          marginBottom: "8px",
          textAlign: "center"
        }}
      >
        {post.title}
      </h2>

      <div
        style={{
          minHeight: "300px",
          border: "1px solid #ddd",
          padding: "20px",
          borderRadius: "6px",
          marginBottom: "16px",
          lineHeight: "1.7",
          fontSize: "1rem",
          whiteSpace: "pre-wrap",
          color: "#333",
          position: "relative",
          display: "flex",
          flexDirection: "column"
        }}
      >
        <div style={{ position: "absolute", top: "12px", right: "20px", fontSize: "0.9rem", color: "#555" }}>
          조회수 {post.views} | 추천수 {post.likes}
        </div>

        <div style={{ flex: "1 0 auto" }}>
          <div dangerouslySetInnerHTML={{ __html: post.content }} />
        </div>

        <div style={{ textAlign: "center", marginTop: "24px", flexShrink: 0 }}>
          <button
            onClick={toggleLike}
            style={{
              padding: "6px 14px",
              backgroundColor: liked ? "#2563eb" : "#e5e7eb",
              color: liked ? "white" : "#111827",
              border: "none",
              borderRadius: "6px",
              cursor: "pointer",
              fontWeight: "600"
            }}
          >
            이 게시물 추천
          </button>
        </div>

        <div style={{
          marginTop: "20px",
          fontSize: "0.9rem",
          color: "#555",
          textAlign: "right"
        }}>
          작성자: {post.nickname} <br />
          {formatFullDateTime(displayDate)}{isEdited && " (수정됨)"}
        </div>
      </div>

      <div style={{ marginTop: "20px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <button
          onClick={() => navigate("/boards")}
          style={{
            padding: "6px 12px",
            backgroundColor: "#e5e7eb",
            color: "#111827",
            border: "1px solid #d1d5db",
            borderRadius: "6px",
            cursor: "pointer"
          }}
        >
          목록
        </button>
        {post.memberId === userId && (
          <div style={{ display: "flex", gap: "10px" }}>
            <button
              onClick={onEdit}
              style={{
                backgroundColor: "#f3f4f6",
                color: "#111827",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                padding: "6px 12px",
                fontWeight: "500"
              }}
            >
              수정
            </button>
            <button
              onClick={onDelete}
              style={{
                backgroundColor: "#fef2f2",
                color: "#b91c1c",
                border: "1px solid #fca5a5",
                borderRadius: "6px",
                padding: "6px 12px",
                fontWeight: "500"
              }}
            >
              삭제
            </button>
          </div>
        )}
      </div>
    </div>
  );
}