// src/pages/BoardPage.js
import { useEffect, useState, useCallback } from "react";
import axios from "axios";
import { Link, useNavigate, useLocation } from "react-router-dom";

// 쿼리 파라미터를 쉽게 읽기 위한 커스텀 훅
function useQuery() {
  return new URLSearchParams(useLocation().search);
}

export default function BoardPage() {
  const navigate = useNavigate();
  const query = useQuery();

  const initialCategory = query.get("category") || "ALL";

  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [category, setCategory] = useState(initialCategory);
  const [sortBy, setSortBy] = useState("createdAt");
  const [direction, setDirection] = useState("desc");
  const [searchType, setSearchType] = useState("title");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [lastSearchParams, setLastSearchParams] = useState({ searchType: "title", searchKeyword: "" });

  const fetchPosts = useCallback(async () => {
    try {
      const params = {
        page,
        sortBy,
        direction,
        ...(category !== "ALL" && { category }),
        ...(lastSearchParams.searchKeyword && {
          searchType: lastSearchParams.searchType,
          searchKeyword: lastSearchParams.searchKeyword,
        }),
      };
      const res = await axios.get("/api/posts/filter", { params });
      setPosts(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error("게시글 조회 실패:", err);
    }
  }, [page, category, sortBy, direction, lastSearchParams]);

  useEffect(() => {
    fetchPosts();
  }, [page, fetchPosts]);

  const handleSearch = () => {
    setPage(1);
    setLastSearchParams({ searchType, searchKeyword: searchKeyword.trim() });
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  const formatDate = (dateString) => {
    const now = new Date();
    const date = new Date(dateString);
    const isToday = now.toDateString() === date.toDateString();

    if (isToday) {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    } else {
      const year = String(date.getFullYear()).slice(2);
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}.${month}.${day}`;
    }
  };

  return (
    <div style={{ padding: "20px", maxWidth: "1200px", margin: "0 auto" }}>
      <div style={{
        position: "relative",
        height: "48px",
        marginBottom: "20px"
      }}>
        <div style={{
          position: "absolute",
          left: 0,
          display: "flex",
          gap: "10px",
          alignItems: "center"
        }}>
<select
  value={`${sortBy}_${direction}`}
  onChange={(e) => {
    const [sort, dir] = e.target.value.split("_");
    setSortBy(sort);
    setDirection(dir);
  }}
  style={{
    padding: "6px",
    border: "1px solid #ccc",
    borderRadius: "4px",
    backgroundColor: "#fff",
    color: "#000",
    cursor: "pointer"
  }}
>
  <option value="createdAt_desc">최신순</option>
  <option value="views_desc">조회순</option>
  <option value="likes_desc">추천순</option>
</select>
        </div>

        <div style={{
          position: "absolute",
          left: "50%",
          transform: "translateX(-50%)",
          display: "flex",
          gap: "10px"
        }}>
          {["ALL", "자유게시판", "스포", "공지사항", "개봉예정작"].map((cat) => (
            <button
              key={cat}
              onClick={() => {
                setCategory(cat);
                setPage(1);
                navigate(`/boards?category=${encodeURIComponent(cat)}`);
              }}
              style={{
                padding: "6px 12px",
                border: "1px solid #ccc",
                borderRadius: "4px",
                backgroundColor: category === cat ? "#007bff" : "#fff",
                color: category === cat ? "#fff" : "#000",
                cursor: "pointer"
              }}
            >
              {cat === "ALL" ? "전체" : cat}
            </button>
          ))}
        </div>

        <div style={{ position: "absolute", right: 0, display: "flex", gap: "10px", alignItems: "center" }}>
<select
  value={searchType}
  onChange={(e) => setSearchType(e.target.value)}
  style={{
    padding: "6px",
    border: "1px solid #ccc",
    borderRadius: "4px",
    backgroundColor: "#fff",
    color: "#000",
    cursor: "pointer"
  }}
>
  <option value="title">제목</option>
  <option value="content">내용</option>
  <option value="nickname">닉네임</option>
</select>

<input
  type="text"
  placeholder="검색어 입력"
  value={searchKeyword}
  onChange={(e) => setSearchKeyword(e.target.value)}
  onKeyDown={handleKeyPress}
  style={{
    padding: "6px",
    border: "1px solid #ccc",
    borderRadius: "4px",
    outline: "none"
  }}
/>
<button
  onClick={handleSearch}
  style={{
    padding: "6px 12px",
    backgroundColor: "#007bff",
    color: "white",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer"
  }}
  onMouseOver={(e) => e.currentTarget.style.backgroundColor = "#0056b3"}
  onMouseOut={(e) => e.currentTarget.style.backgroundColor = "#007bff"}
>
  검색
</button>
        </div>
      </div>

      <table style={{ width: "100%", borderCollapse: "collapse", marginBottom: "20px" }}>
        <thead>
          <tr style={{ backgroundColor: "#f5f5f5" }}>
            <th style={{ padding: "8px", textAlign: "center", width: "5%" }}>#</th>
            <th style={{ padding: "8px", textAlign: "center", width: "10%" }}>카테고리</th>
            <th style={{ padding: "8px", textAlign: "center" }}>제목</th>
            <th style={{ padding: "8px", textAlign: "center", width: "10%" }}>닉네임</th>
            <th style={{ padding: "8px", textAlign: "center", width: "10%" }}>날짜</th>
            <th style={{ padding: "8px", textAlign: "center", width: "7%" }}>조회</th>
            <th style={{ padding: "8px", textAlign: "center", width: "7%" }}>추천</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((post) => (
            <tr key={post.postNo}>
              <td style={{ padding: "8px", textAlign: "center" }}>{post.postNo}</td>
              <td style={{ padding: "8px", textAlign: "center" }}>{post.category}</td>
              <td style={{ padding: "8px", textAlign: "left" }}>
                <Link to={`/boards/${post.postNo}`} style={{ color: "#007bff", textDecoration: "none" }}>
                  {post.title}
                </Link>
              </td>
              <td style={{ padding: "8px", textAlign: "center" }}>{post.nickname}</td>
              <td style={{ padding: "8px", textAlign: "center" }}>{formatDate(post.createdAt)}</td>
              <td style={{ padding: "8px", textAlign: "center" }}>{post.views}</td>
              <td style={{ padding: "8px", textAlign: "center" }}>{post.likes}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "16px" }}>
        <div style={{ display: "flex", justifyContent: "center", flexGrow: 1, gap: "6px", flexWrap: "wrap" }}>
          <button
            disabled={page === 1}
            onClick={() => setPage(page - 1)}
            style={{
              padding: "6px 10px",
              border: "1px solid #aaa",
              backgroundColor: page === 1 ? "#eee" : "#fff",
              color: page === 1 ? "#999" : "#000",
              borderRadius: "4px",
              cursor: page === 1 ? "not-allowed" : "pointer"
            }}
          >
            {"<"}
          </button>

          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i + 1}
              onClick={() => setPage(i + 1)}
              style={{
                padding: "6px 10px",
                border: "1px solid #aaa",
                backgroundColor: page === i + 1 ? "#000" : "#fff",
                color: page === i + 1 ? "#fff" : "#000",
                borderRadius: "4px",
                cursor: "pointer"
              }}
            >
              {i + 1}
            </button>
          ))}

          <button
            disabled={page === totalPages}
            onClick={() => setPage(page + 1)}
            style={{
              padding: "6px 10px",
              border: "1px solid #aaa",
              backgroundColor: page === totalPages ? "#eee" : "#fff",
              color: page === totalPages ? "#999" : "#000",
              borderRadius: "4px",
              cursor: page === totalPages ? "not-allowed" : "pointer"
            }}
          >
            {">"}
          </button>
        </div>

        <div style={{ marginLeft: "auto" }}>
          <button
            style={{
              padding: "8px 16px",
              backgroundColor: "#007bff",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer"
            }}
            onClick={() =>
    navigate(category === "ALL" ? "/boards/write" : `/boards/write?category=${encodeURIComponent(category)}`)
  }
          >
            글쓰기
          </button>
        </div>
      </div>
    </div>
  );
}