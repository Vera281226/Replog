import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

export default function PostDetailPage() {
  const { postNo } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [liked, setLiked] = useState(false);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("success");
  const [showConfirm, setShowConfirm] = useState(false);
  const [commentToDelete, setCommentToDelete] = useState(null);
  const [editingCommentNo, setEditingCommentNo] = useState(null);
  const [editingContent, setEditingContent] = useState("");
  const userId = "user01";

  useEffect(() => {
    const increaseViews = async () => {
      try {
        await axios.post(`/api/posts/${postNo}/view`);
      } catch (err) {
        console.error("조회수 증가 실패:", err);
      }
    };

    const fetchPost = async () => {
      try {
        const res = await axios.get(`/api/posts/${postNo}`);
        setPost(res.data);
      } catch (err) {
        showMessage("게시글을 불러오지 못했습니다.", "error");
        navigate("/boards");
      }
    };

    const fetchComments = async () => {
      try {
        const res = await axios.get(`/api/comments/post/${postNo}`, {
          params: { memberId: userId },
        });
        setComments(res.data);
      } catch (err) {
        console.error("댓글을 불러오지 못했습니다.", err);
      }
    };

    const checkLiked = async () => {
      try {
        const res = await axios.get(`/api/posts/${postNo}/like`, {
          params: { memberId: userId },
        });
        setLiked(res.data);
      } catch (err) {
        console.error("좋아요 상태 확인 실패:", err);
      }
    };

    increaseViews();
    fetchPost();
    fetchComments();
    if (userId) checkLiked();
  }, [postNo, navigate]);

  const showMessage = (msg, type = "success") => {
    setMessage(msg);
    setMessageType(type);
    setTimeout(() => setMessage(""), 3000);
  };

  const toggleLike = async () => {
    try {
      const res = await axios.post(`/api/posts/${postNo}/like`, null, {
        params: { memberId: userId },
      });
      const isLiked = res.data === "liked";
      setLiked(isLiked);
      setPost((prev) => ({
        ...prev,
        likes: isLiked ? prev.likes + 1 : prev.likes - 1,
      }));
    } catch (err) {
      showMessage("좋아요 처리에 실패했습니다.", "error");
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
      showMessage("댓글 좋아요 처리에 실패했습니다.", "error");
    }
  };

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
      showMessage("댓글이 삭제되었습니다.");
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
            ? {
                ...c,
                content: res.data.content,
                updatedAt: res.data.updatedAt,
              }
            : c
        )
      );
      setEditingCommentNo(null);
      setEditingContent("");
      showMessage("댓글이 수정되었습니다.");
    } catch (err) {
      showMessage("댓글 수정에 실패했습니다.", "error");
    }
  };

  const confirmDelete = async () => {
    try {
      await axios.delete(`/api/posts/${postNo}`);
      showMessage("삭제되었습니다.", "success");
      setShowConfirm(false);
      setTimeout(() => navigate("/boards"), 1000);
    } catch (err) {
      showMessage("삭제에 실패했습니다.", "error");
      setShowConfirm(false);
    }
  };

  if (!post) return <div>로딩 중...</div>;

  return (
    <div style={{ padding: "20px", maxWidth: "800px", margin: "0 auto" }}>
      {message && (
        <div
          style={{
            marginBottom: "20px",
            padding: "12px",
            backgroundColor:
              messageType === "success" ? "#d4edda" : "#f8d7da",
            color: messageType === "success" ? "#155724" : "#721c24",
            border: `1px solid ${
              messageType === "success" ? "#c3e6cb" : "#f5c6cb"
            }`,
            borderRadius: "4px",
            textAlign: "center",
          }}
        >
          {message}
        </div>
      )}

      <h2>{post.title}</h2>
      <div style={{ marginBottom: "10px", color: "#555" }}>
        {post.nickname} |{" "}
  {new Date(post.updatedAt ?? post.createdAt).toLocaleString()}
  {post.updatedAt && " (수정됨)"} | 조회 {post.views} | 추천 {post.likes}
        {userId && (
          <button
            onClick={toggleLike}
            style={{
              marginLeft: "10px",
              padding: "4px 10px",
              fontSize: "0.9rem",
              backgroundColor: liked ? "#ff6b6b" : "#e0e0e0",
              color: liked ? "white" : "black",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            {liked ? "좋아요 취소" : "좋아요"}
          </button>
        )}
      </div>
      <hr />
      <div
  style={{ marginTop: "20px" }}
  dangerouslySetInnerHTML={{ __html: post.content }}
/>

      <hr style={{ margin: "30px 0" }} />
      {post.memberId === userId && (
        <div style={{ marginBottom: "10px", display: "flex", gap: "10px" }}>
          <button
            onClick={() => {
              navigate(`/posts/edit/${post.postNo}`, { state: { post } });
            }}
            style={{
              padding: "6px 12px",
              backgroundColor: "#ffc107",
              color: "#212529",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            수정
          </button>
          <button
            onClick={() => setShowConfirm(true)}
            style={{
              padding: "6px 12px",
              backgroundColor: "#dc3545",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            삭제
          </button>
        </div>
      )}

      <h3>댓글</h3>
      <div>
        {comments.map((comment) => {
          const isEdited = comment.updatedAt !== null;
          const timeLabel = isEdited ? comment.updatedAt : comment.createdAt;

          return (
            <div
              key={comment.commentNo}
              style={{
                marginBottom: "12px",
                padding: "10px",
                border: "1px solid #ddd",
                borderRadius: "4px",
              }}
            >
              <div style={{ fontWeight: "bold" }}>{comment.nickname}</div>
              <div style={{ fontSize: "0.9rem", color: "#666" }}>
                {new Date(timeLabel).toLocaleString()} {isEdited && "(수정됨)"}
              </div>
              {editingCommentNo === comment.commentNo ? (
                <>
                  <textarea
                    value={editingContent}
                    onChange={(e) => setEditingContent(e.target.value)}
                    rows={3}
                    style={{ width: "100%", padding: "8px" }}
                  />
                  <div style={{ marginTop: "6px" }}>
                    <button
                      onClick={() => handleUpdateComment(comment.commentNo)}
                      style={{
                        padding: "4px 10px",
                        marginRight: "8px",
                        backgroundColor: "#28a745",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        cursor: "pointer",
                      }}
                    >
                      완료
                    </button>
                    <button
                      onClick={() => setEditingCommentNo(null)}
                      style={{
                        padding: "4px 10px",
                        backgroundColor: "#6c757d",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        cursor: "pointer",
                      }}
                    >
                      취소
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <div style={{ marginTop: "6px" }}>{comment.content}</div>
                  <div style={{ marginTop: "6px" }}>
                    추천 {comment.likes}
                    {userId && (
                      <button
                        onClick={() => toggleCommentLike(comment.commentNo, comment.isLiked)}
                        style={{
                          marginLeft: "10px",
                          padding: "4px 10px",
                          fontSize: "0.8rem",
                          backgroundColor: comment.isLiked ? "#ff6b6b" : "#e0e0e0",
                          color: comment.isLiked ? "white" : "black",
                          border: "none",
                          borderRadius: "4px",
                          cursor: "pointer",
                        }}
                      >
                        {comment.isLiked ? "좋아요 취소" : "좋아요"}
                      </button>
                    )}
                    {comment.memberId === userId && (
                      <>
                        <button
                          onClick={() => {
                            setEditingCommentNo(comment.commentNo);
                            setEditingContent(comment.content);
                          }}
                          style={{
                            marginLeft: "10px",
                            padding: "4px 10px",
                            fontSize: "0.8rem",
                            backgroundColor: "#ffc107",
                            color: "black",
                            border: "none",
                            borderRadius: "4px",
                            cursor: "pointer",
                          }}
                        >
                          수정
                        </button>
                        <button
                          onClick={() => setCommentToDelete(comment.commentNo)}
                          style={{
                            marginLeft: "10px",
                            padding: "4px 10px",
                            fontSize: "0.8rem",
                            backgroundColor: "#dc3545",
                            color: "white",
                            border: "none",
                            borderRadius: "4px",
                            cursor: "pointer",
                          }}
                        >
                          삭제
                        </button>
                      </>
                    )}
                  </div>
                </>
              )}
            </div>
          );
        })}
      </div>

      <div
        style={{
          marginTop: "20px",
          display: "flex",
          flexDirection: "column",
          gap: "10px",
        }}
      >
        <textarea
          placeholder="댓글을 입력하세요"
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          rows={3}
          style={{ width: "100%", padding: "10px" }}
        />
        <button
          onClick={handleCommentSubmit}
          style={{
            alignSelf: "flex-end",
            padding: "8px 16px",
            backgroundColor: "#28a745",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          댓글 작성
        </button>
      </div>

      <button
        onClick={() => navigate("/boards")}
        style={{
          marginTop: "30px",
          padding: "8px 16px",
          backgroundColor: "#007bff",
          color: "white",
          border: "none",
          borderRadius: "4px",
          cursor: "pointer",
        }}
      >
        목록으로
      </button>

      {(showConfirm || commentToDelete !== null) && (
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
            <p style={{ marginBottom: "20px" }}>
              {commentToDelete
                ? "댓글을 삭제하시겠습니까?"
                : "정말 삭제하시겠습니까?"}
            </p>
            <div
              style={{ display: "flex", justifyContent: "center", gap: "20px" }}
            >
              <button
                onClick={commentToDelete ? handleDeleteComment : confirmDelete}
                style={{
                  padding: "8px 16px",
                  backgroundColor: "#dc3545",
                  color: "white",
                  border: "none",
                  borderRadius: "4px",
                  cursor: "pointer",
                }}
              >
                예
              </button>
              <button
                onClick={() => {
                  setShowConfirm(false);
                  setCommentToDelete(null);
                }}
                style={{
                  padding: "8px 16px",
                  backgroundColor: "#6c757d",
                  color: "white",
                  border: "none",
                  borderRadius: "4px",
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
