import React, { useState, useEffect } from "react"; 
import { useParams, useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { selectCurrentUser, selectIsAuthenticated } from "../error/redux/authSlice";
import axios from "../error/api/interceptor";
import PostContent from "../components/PostContent";
import CommentSection from "../components/CommentSection";
import ReportButton from "../components/common/ReportButton";
import { ErrorModal } from "../error/components/ErrorModal";
import "./css/PostDetailPage.css";

export default function PostDetailPage() {
  const { postNo } = useParams();
  const navigate = useNavigate();

  const currentUser = useSelector(selectCurrentUser);
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const userId = currentUser?.memberId || null;
  const nickname = currentUser?.nickname || "";

  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [liked, setLiked] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  // ✅ 추가된 모달 상태
  const [alertModalOpen, setAlertModalOpen] = useState(false);
  const [alertModalTitle, setAlertModalTitle] = useState("");
  const [alertModalMessage, setAlertModalMessage] = useState("");

  const openAlertModal = (message, title = "알림") => {
    setAlertModalTitle(title);
    setAlertModalMessage(message);
    setAlertModalOpen(true);
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const postRes = await axios.get(`/posts/${postNo}`);

        if (!isAuthenticated) {
          setPost(postRes.data);
          setComments([]);
          setLiked(false);
          return;
        }

        const [commentsRes, likedRes] = await Promise.all([
          axios.get(`/comments/post/${postNo}`, {
            params: { memberId: userId },
          }),
          axios.get(`/posts/${postNo}/like`, {
            params: { memberId: userId },
          }),
        ]);

        setPost(postRes.data);
        setComments(commentsRes.data);
        setLiked(likedRes.data);
      } catch (err) {
        console.error("데이터 로딩 실패:", err);
        openAlertModal("데이터를 불러오지 못했습니다.", "오류");
        setTimeout(() => navigate("/boards"), 2000);
      }
    };

    if (postNo) fetchData();
  }, [postNo, userId, isAuthenticated, navigate]);

  const toggleLike = async () => {
    if (!isAuthenticated) {
      openAlertModal("로그인이 필요합니다.", "알림");
      return;
    }

    try {
      const res = await axios.post(`/posts/${postNo}/like`, null, {
        params: { memberId: userId },
      });
      const isLiked = res.data === "liked";
      setLiked(isLiked);
      setPost((prev) => ({
        ...prev,
        likes: isLiked ? prev.likes + 1 : prev.likes - 1,
      }));
    } catch (err) {
      openAlertModal("좋아요 처리 실패", "오류");
    }
  };

  const confirmDelete = async () => {
    try {
      await axios.delete(`/posts/${postNo}`);
      navigate("/boards");
    } catch (err) {
      openAlertModal("삭제에 실패했습니다.", "오류");
    } finally {
      setShowConfirm(false);
    }
  };

  if (!post) return <div>로딩 중...</div>;

  const categoryList = ["ALL", "자유게시판", "스포", "공지사항", "개봉예정작"];
  const isAdmin = currentUser?.role === "ROLE_ADMIN"; // 관리자 여부
  const isAuthor = userId === post.memberId;

  return (
    <div className="post-detail-page">
      <div className="category-buttons">
        {categoryList.map((cat) => (
          <button
            key={cat}
            onClick={() => navigate(`/boards?category=${encodeURIComponent(cat)}`)}
            className={`category-button ${
              post.category === cat || (cat === "ALL" && post.category === "ALL")
                ? "active"
                : ""
            }`}
          >
            {cat === "ALL" ? "전체" : cat}
          </button>
        ))}
      </div>

      {isAuthenticated && (
        <div className="report-button-wrapper">
          <ReportButton
            targetType="POST"
            targetId={String(post.postNo)}
            buttonStyle="text"
            buttonText="🚨 신고"
          />
        </div>
      )}

      <PostContent
        post={post}
        liked={liked}
        toggleLike={toggleLike}
        onEdit={() => navigate(`/boards/edit/${post.postNo}`, { state: { post } })}
        onDelete={() => setShowConfirm(true)}
        isAuthor={isAuthor}
        isAdmin={isAdmin} // ✅ 추가
        isAuthenticated={isAuthenticated}
      />

      <CommentSection
        postNo={postNo}
        userId={userId}
        nickname={nickname}
        comments={comments}
        setComments={setComments}
        showMessage={(msg, type) => {
          if (type === "error") {
            openAlertModal(msg, "오류");
          } else {
            openAlertModal(msg, "알림");
          }
        }}
        isAuthenticated={isAuthenticated}
      />

      {/* 게시글 삭제 확인 모달 */}
      <ErrorModal
        isOpen={showConfirm}
        title="게시글 삭제 확인"
        message="정말 삭제하시겠습니까?"
        onConfirm={confirmDelete}
        onCancel={() => setShowConfirm(false)}
      />

      {/* 공통 알림 모달 */}
      <ErrorModal
        isOpen={alertModalOpen}
        title={alertModalTitle}
        message={alertModalMessage}
        onConfirm={() => setAlertModalOpen(false)}
        onCancel={() => setAlertModalOpen(false)}
      />
    </div>
  );
}