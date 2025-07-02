// src/pages/PostDetailPage.js
import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import PostContent from "../components/PostContent";
import CommentSection from "../components/CommentSection";
import ReportButton from "../components/common/ReportButton";

export default function PostDetailPage() {
  const { postNo } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [liked, setLiked] = useState(false);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("success");
  const [showConfirm, setShowConfirm] = useState(false);
  const userId = "user01";

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [postRes, commentsRes, likedRes] = await Promise.all([
          axios.get(`/api/posts/${postNo}`),
          axios.get(`/api/comments/post/${postNo}`, {
            params: { memberId: userId },
          }),
          axios.get(`/api/posts/${postNo}/like`, {
            params: { memberId: userId },
          }),
        ]);

        setPost(postRes.data);
        setComments(commentsRes.data);
        setLiked(likedRes.data);
      } catch (err) {
        console.error("데이터 로딩 실패:", err);
        setMessage("데이터를 불러오지 못했습니다.");
        setMessageType("error");
        setTimeout(() => navigate("/boards"), 2000);
      }
    };

    fetchData();
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
      showMessage("좋아요 처리 실패", "error");
    }
  };

  const confirmDelete = async () => {
    try {
      await axios.delete(`/api/posts/${postNo}`);
      navigate("/boards");
    } catch (err) {
      showMessage("삭제에 실패했습니다.", "error");
    } finally {
      setShowConfirm(false);
    }
  };

  if (!post) return <div>로딩 중...</div>;

  const categoryList = ["ALL", "자유게시판", "스포", "공지사항", "개봉예정작"];

  return (
    <div style={{ padding: "20px", maxWidth: "800px", margin: "0 auto" }}>
      {message && (
        <div
          style={{
            marginBottom: "20px",
            padding: "12px",
            backgroundColor: messageType === "success" ? "#d4edda" : "#f8d7da",
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
<div style={{ textAlign: "right", marginBottom: "12px" }}>
        <ReportButton
          targetType="POST"
          targetId={String(post.postNo)}
          buttonStyle="text"
          buttonText="🚨 신고"
        />
      </div>
      {/* 카테고리 버튼 UI */}
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          gap: "10px",
          marginBottom: "20px",
        }}
      >
        {categoryList.map((cat) => (
          <button
            key={cat}
            onClick={() => navigate(`/boards?category=${encodeURIComponent(cat)}`)}
            style={{
              padding: "6px 12px",
              border: "1px solid #ccc",
              borderRadius: "4px",
              backgroundColor: post.category === cat || (cat === "ALL" && post.category === "ALL")
                ? "#007bff"
                : "#fff",
              color: post.category === cat || (cat === "ALL" && post.category === "ALL")
                ? "#fff"
                : "#000",
              cursor: "pointer",
            }}
          >
            {cat === "ALL" ? "전체" : cat}
          </button>
        ))}
      </div>

      <PostContent
        post={post}
        userId={userId}
        liked={liked}
        toggleLike={toggleLike}
        onEdit={() => navigate(`/posts/edit/${post.postNo}`, { state: { post } })}
        onDelete={() => setShowConfirm(true)}
      />

      <CommentSection
        postNo={postNo}
        userId={userId}
        comments={comments}
        setComments={setComments}
        showMessage={showMessage}
      />

      {showConfirm && (
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
            <p style={{ marginBottom: "20px" }}>정말 삭제하시겠습니까?</p>
            <div
              style={{
                display: "flex",
                justifyContent: "center",
                gap: "20px",
              }}
            >
              <button
                onClick={confirmDelete}
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
                onClick={() => setShowConfirm(false)}
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
