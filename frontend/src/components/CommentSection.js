import { useState } from "react";
import axios from "axios";

export default function CommentSection({ postNo, userId, comments, setComments, showMessage }) {
  const [newComment, setNewComment] = useState("");
  const [editingCommentNo, setEditingCommentNo] = useState(null);
  const [editingContent, setEditingContent] = useState("");
  const [commentToDelete, setCommentToDelete] = useState(null);

  const handleCommentSubmit = async () => {
    if (!newComment.trim()) return;
    try {
      const res = await axios.post("/api/comments", {
        memberId: userId,
        nickname: "길동이",
        postNo: parseInt(postNo),
        content: newComment,
      });
      setComments((prev) => [
        ...prev,
        { ...res.data, likes: 0, isLiked: false },
      ]);
      setNewComment("");
    } catch (err) {
      showMessage("댓글 작성에 실패했습니다.", "error");
    }
  };

  const handleDeleteComment = async () => {
    try {
      await axios.delete(`/api/comments/${commentToDelete}`);
      setComments((prev) => prev.filter((c) => c.commentNo !== commentToDelete));
    } catch (err) {
      showMessage("댓글 삭제에 실패했습니다.", "error");
    } finally {
      setCommentToDelete(null);
    }
  };

  const handleUpdateComment = async (commentNo) => {
    try {
      const res = await axios.put(`/api/comments/${commentNo}`, {
        memberId: userId,
        nickname: "길동이",
        postNo: parseInt(postNo),
        content: editingContent,
      });
      setComments((prev) =>
        prev.map((c) =>
          c.commentNo === commentNo
            ? { ...c, content: res.data.content, updatedAt: res.data.updatedAt }
            : c
        )
      );
      setEditingCommentNo(null);
      setEditingContent("");
      showMessage("댓글이 수정되었습니다.", "error");
    } catch (err) {
      showMessage("댓글 수정에 실패했습니다.", "error");
    }
  };

  const toggleCommentLike = async (commentNo, currentIsLiked) => {
    try {
      const res = await axios.post(`/api/comments/${commentNo}/like`, null, {
        params: { memberId: userId },
      });
      const isLiked = res.data === "liked";
      setComments((prevComments) =>
        prevComments.map((comment) =>
          comment.commentNo === commentNo
            ? {
                ...comment,
                isLiked,
                likes: isLiked ? comment.likes + 1 : comment.likes - 1,
              }
            : comment
        )
      );
    } catch (err) {
      showMessage("댓글 좋아요 처리 실패", "error");
    }
  };

  return (
    <div style={{ marginTop: "30px" }}>
      <h3>댓글</h3>
      {comments.map((comment) => {
        const isEdited = comment.updatedAt !== null;
        const timeLabel = isEdited ? comment.updatedAt : comment.createdAt;

        return (
          <div
            key={comment.commentNo}
            style={{
              marginBottom: "12px",
              padding: "14px",
              border: "1px solid #e0e0e0",
              borderRadius: "8px",
              backgroundColor: "#ffffff",
              boxShadow: "0 1px 2px rgba(0, 0, 0, 0.05)",
              overflow: "hidden"
            }}
          >
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap" }}>
              <div style={{ display: "flex", gap: "10px", fontWeight: "500", fontSize: "1rem", alignItems: "center" }}>
                <span>{comment.nickname}</span>
                <span style={{ fontSize: "0.875rem", color: "#888" }}>{new Date(timeLabel).toLocaleString()} {isEdited && "(수정됨)"}</span>
              </div>
              <button
                onClick={() => toggleCommentLike(comment.commentNo, comment.isLiked)}
                style={{
                  border: "none",
                  background: "transparent",
                  cursor: "pointer",
                  fontSize: "1rem",
                  display: "flex",
                  alignItems: "center",
                  color: comment.isLiked ? "#2563eb" : "#9ca3af",
                  fontWeight: "600"
                }}
              >
                <span style={{ fontSize: "1.2rem", marginRight: "6px" }}></span>
                <span>{`추천 ${comment.likes}`}</span>
              </button>
            </div>

            {editingCommentNo === comment.commentNo ? (
              <>
                <textarea
                  value={editingContent}
                  onChange={(e) => setEditingContent(e.target.value)}
                  rows={3}
                  style={{
                    width: "100%",
                    padding: "10px",
                    border: "1px solid #ccc",
                    borderRadius: "6px",
                    marginTop: "10px",
                    resize: "vertical",
                    boxSizing: "border-box"
                  }}
                />
                <div style={{ marginTop: "8px", display: "flex", gap: "8px", justifyContent: "flex-end" }}>
                  <button
                    onClick={() => handleUpdateComment(comment.commentNo)}
                    style={{ backgroundColor: "#2563eb", color: "white", border: "none", borderRadius: "6px", padding: "6px 12px" }}
                  >완료</button>
                  <button
                    onClick={() => setEditingCommentNo(null)}
                    style={{ backgroundColor: "#9ca3af", color: "white", border: "none", borderRadius: "6px", padding: "6px 12px" }}
                  >취소</button>
                </div>
              </>
            ) : (
              <div style={{ marginTop: "8px", display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap" }}>
                <div style={{ wordBreak: "break-word", flex: 1 }}>{comment.content}</div>
                {comment.memberId === userId && (
                  <div style={{ display: "flex", gap: "8px", marginLeft: "10px", flexShrink: 0 }}>
                    <button
                      onClick={() => {
                        setEditingCommentNo(comment.commentNo);
                        setEditingContent(comment.content);
                      }}
                      style={{ backgroundColor: "#f3f4f6", color: "#111827", border: "1px solid #d1d5db", borderRadius: "6px", padding: "4px 10px" }}
                    >수정</button>
                    <button
                      onClick={() => setCommentToDelete(comment.commentNo)}
                      style={{ backgroundColor: "#fef2f2", color: "#b91c1c", border: "1px solid #fca5a5", borderRadius: "6px", padding: "4px 10px" }}
                    >삭제</button>
                  </div>
                )}
              </div>
            )}
          </div>
        );
      })}

      <div style={{ marginTop: "20px", display: "flex", flexDirection: "column", gap: "10px" }}>
        <textarea
          placeholder="댓글을 입력하세요"
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          rows={3}
          style={{ width: "100%", padding: "10px", borderRadius: "6px", border: "1px solid #ccc", resize: "vertical", boxSizing: "border-box" }}
        />
        <button
          onClick={handleCommentSubmit}
          style={{
            alignSelf: "flex-end",
            padding: "10px 16px",
            backgroundColor: "#10b981",
            color: "white",
            border: "none",
            borderRadius: "6px",
            cursor: "pointer",
            fontWeight: "600"
          }}
        >
          댓글 작성
        </button>
      </div>

      {commentToDelete !== null && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            width: "100vw",
            height: "100vh",
            backgroundColor: "rgba(0,0,0,0.5)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 1000,
          }}
        >
          <div
            style={{
              backgroundColor: "white",
              padding: "20px",
              borderRadius: "8px",
              textAlign: "center",
              boxShadow: "0 0 10px rgba(0,0,0,0.2)",
            }}
          >
            <p style={{ marginBottom: "20px" }}>댓글을 삭제하시겠습니까?</p>
            <div style={{ display: "flex", justifyContent: "center", gap: "20px" }}>
              <button
                onClick={handleDeleteComment}
                style={{
                  padding: "8px 16px",
                  backgroundColor: "#ef4444",
                  color: "white",
                  border: "none",
                  borderRadius: "6px",
                  cursor: "pointer",
                }}
              >
                예
              </button>
              <button
                onClick={() => setCommentToDelete(null)}
                style={{
                  padding: "8px 16px",
                  backgroundColor: "#9ca3af",
                  color: "white",
                  border: "none",
                  borderRadius: "6px",
                  cursor: "pointer",
                }}
              >
                아니오
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
