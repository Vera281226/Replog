import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "../error/api/interceptor";
import { ErrorModal } from "../error/components/ErrorModal"; // ✅ 추가
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "../error/redux/authSlice";
import ReportButton from "../components/common/ReportButton";
import "./css/PartyPostDetailPage.css";

const PartyPostDetailPage = () => {
  const { partyPostNo } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const isAuthenticated = useSelector(selectIsAuthenticated);

  // ✅ 에러 모달 상태
  const [errorModalOpen, setErrorModalOpen] = useState(false);
  const [errorModalMessage, setErrorModalMessage] = useState("");

  const openErrorModal = (message) => {
    setErrorModalMessage(message);
    setErrorModalOpen(true);
  };

  useEffect(() => {
    axios
      .get(`/partyposts/${partyPostNo}`)
      .then((res) => setPost(res.data))
      .catch(() => {
        openErrorModal("게시글을 불러오지 못했습니다.");
        navigate(-1);
      });
  }, [partyPostNo, navigate]);

  const handleJoinClick = async () => {
    try {
      const res = await axios.get(`/chat/rooms/party-post/${post.partyPostNo}`);
      const { chatRoomId } = res.data;
      await axios.post(
        `/chat/rooms/${chatRoomId}/join`,
        {},
        { withCredentials: true }
      );
      openErrorModal("채팅방에 참가되었습니다!");
      // navigate(`/chat/${chatRoomId}`);
    } catch (err) {
      if (err.response?.status === 409) {
        openErrorModal("이미 채팅방에 참가한 사용자입니다.");
      } else {
        openErrorModal("채팅방 참가에 실패했습니다.");
      }
    }
  };

  if (!post)
    return <p style={{ textAlign: "center", marginTop: "50px" }}>로딩 중...</p>;

  return (
    <div className="detail-container">
      <div className="detail-card">

{isAuthenticated && (
  <div className="report-button-wrapper" style={{ marginTop: "10px", textAlign: "right" }}>
    <ReportButton
      targetType="PARTY_POST"
      targetId={String(post.partyPostNo)}
      buttonStyle="text"
      buttonText="🚨 신고"
    />
  </div>
)}
        
        <h2 className="detail-title">{post.title}</h2>

        <div className="detail-meta">
          <div>
            <strong>👤 작성자:</strong> {post.nickname}
          </div>
          <div>
            <strong>🕒 작성일:</strong>{" "}
            {new Date(post.createdAt).toLocaleString()}
          </div>
          <div>
            <strong>👁️ 조회수:</strong> {post.views}
          </div>
        </div>

        <table className="detail-table">
          <tbody>
            <tr>
              <th>🎬 영화명</th>
              <td>{post.movie}</td>
            </tr>
            <tr>
              <th>📅 모집 마감일</th>
              <td>{new Date(post.partyDeadline).toLocaleString()}</td>
            </tr>
            <tr>
              <th>👥 모집 인원</th>
              <td>{post.partyLimit}명</td>
            </tr>
            <tr>
              <th>⚧ 성별 제한</th>
              <td>{post.gender || "무관"}</td>
            </tr>
            <tr>
              <th>🎂 연령대 제한</th>
              <td>{getAgeGroups(post.ageGroupsMask)}</td>
            </tr>
          </tbody>
        </table>

        <div className="detail-content">
          <h4 style={{ marginBottom: "10px" }}>📝 본문 내용</h4>
          <div
            className="detail-text"
            dangerouslySetInnerHTML={{ __html: post.content }}
          />
        </div>

        <button className="detail-join-button" onClick={handleJoinClick}>
          참가하기
        </button>

        <div className="detail-back">
          <button onClick={() => navigate(-1)} className="detail-back-button">
            ← 목록으로 돌아가기
          </button>
        </div>
      </div>

      {/* 에러 모달 */}
      <ErrorModal
        isOpen={errorModalOpen}
        title="알림"
        message={errorModalMessage}
        onConfirm={() => setErrorModalOpen(false)}
        onCancel={() => setErrorModalOpen(false)}
      />
    </div>
  );
};

// 연령대 마스크 해석 함수
const getAgeGroups = (mask) => {
  if (mask === 0 || mask == null) return "무관";
  const ageGroups = [];
  if (mask & 1) ageGroups.push("10대");
  if (mask & 2) ageGroups.push("20대");
  if (mask & 4) ageGroups.push("30대");
  if (mask & 8) ageGroups.push("40대");
  if (mask & 16) ageGroups.push("50대");
  if (mask & 32) ageGroups.push("60대");
  return ageGroups.join(", ");
};

export default PartyPostDetailPage;