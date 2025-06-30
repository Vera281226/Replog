import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

const PartyPostDetailPage = () => {
  const { partyPostNo } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);

  useEffect(() => {
    axios.get(`/api/partyposts/${partyPostNo}`)
      .then((res) => setPost(res.data))
      .catch((err) => {
        console.error("상세글 조회 실패", err);
        alert("게시글을 불러오지 못했습니다.");
        navigate(-1);
      });
  }, [partyPostNo, navigate]);

  if (!post) return <p style={{ textAlign: "center", marginTop: "50px" }}>로딩 중...</p>;

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h2 style={styles.title}>{post.title}</h2>

        <div style={styles.meta}>
          <div><strong>👤 작성자:</strong> {post.nickname}</div>
          <div><strong>🕒 작성일:</strong> {new Date(post.createdAt).toLocaleString()}</div>
          <div><strong>👁️ 조회수:</strong> {post.views}</div>
        </div>

        <table style={styles.table}>
          <tbody>
            <tr>
              <th style={styles.th}>🎬 영화명</th>
              <td style={styles.td}>{post.movie}</td>
            </tr>
            <tr>
              <th style={styles.th}>📅 모집 마감일</th>
              <td style={styles.td}>{new Date(post.partyDeadline).toLocaleString()}</td>
            </tr>
            <tr>
              <th style={styles.th}>👥 모집 인원</th>
              <td style={styles.td}>{post.partyLimit}명</td>
            </tr>
            <tr>
              <th style={styles.th}>⚧ 성별 제한</th>
              <td style={styles.td}>{post.gender || "무관"}</td>
            </tr>
            <tr>
              <th style={styles.th}>🎂 연령대 제한</th>
              <td style={styles.td}>{getAgeGroups(post.ageGroupsMask)}</td>
            </tr>
          </tbody>
        </table>

        <div style={styles.content}>
          <h4 style={{ marginBottom: "10px" }}>📝 본문 내용</h4>
          <div
  style={styles.text}
  dangerouslySetInnerHTML={{ __html: post.content }}
/>
        </div>

        <div style={{ textAlign: "right" }}>
          <button onClick={() => navigate(-1)} style={styles.backButton}>
            ← 목록으로 돌아가기
          </button>
        </div>
      </div>
    </div>
  );
};

// 💡 ageGroups 변환 함수는 그대로
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

// 🎨 스타일
const styles = {
  container: {
    backgroundColor: "#f4f6f8",
    minHeight: "100vh",
    padding: "40px 20px",
    display: "flex",
    justifyContent: "center",
  },
  card: {
    backgroundColor: "#fff",
    width: "800px",
    borderRadius: "12px",
    boxShadow: "0 6px 18px rgba(0,0,0,0.08)",
    padding: "30px",
    boxSizing: "border-box",
  },
  title: {
    fontSize: "28px",
    fontWeight: "600",
    marginBottom: "16px",
    borderBottom: "2px solid #ddd",
    paddingBottom: "8px",
    color: "#222",
  },
  meta: {
    display: "flex",
    justifyContent: "space-between",
    fontSize: "14px",
    color: "#666",
    marginBottom: "20px",
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
    marginBottom: "30px",
  },
  th: {
    backgroundColor: "#f9f9f9",
    padding: "10px",
    textAlign: "left",
    width: "160px",
    borderBottom: "1px solid #e0e0e0",
    color: "#444",
  },
  td: {
    padding: "10px",
    borderBottom: "1px solid #e0e0e0",
  },
  content: {
    marginBottom: "30px",
  },
  text: {
    backgroundColor: "#f0f0f0",
    padding: "15px",
    borderRadius: "8px",
    fontSize: "16px",
    lineHeight: "1.6",
    whiteSpace: "pre-wrap",
    color: "#333",
  },
  backButton: {
    backgroundColor: "#007bff",
    color: "#fff",
    border: "none",
    padding: "10px 20px",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "14px",
    transition: "background-color 0.3s",
  },
};

export default PartyPostDetailPage;