import { useEffect, useState, useCallback } from "react";
import axios from "axios";
import {Link, useNavigate } from "react-router-dom";

export default function BoardPage() {
  const navigate = useNavigate();
  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [category, setCategory] = useState("ALL");
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

  return (
    <div style={{ padding: "20px", maxWidth: "1200px", margin: "0 auto" }}>
      <div style={{ marginBottom: "20px", display: "flex", justifyContent: "space-between", flexWrap: "wrap", gap: "10px" }}>
        <div style={{ display: "flex", gap: "10px", flexWrap: "wrap" }}>
          <select value={category} onChange={(e) => { setCategory(e.target.value); setPage(1); }}>
            <option value="ALL">전체</option>
            <option value="자유게시판">자유게시판</option>
            <option value="스포">스포</option>
            <option value="공지사항">공지사항</option>
            <option value="개봉예정작">개봉예정작</option>
          </select>

          <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
            <option value="createdAt">최신순</option>
            <option value="views">조회순</option>
            <option value="likes">추천순</option>
          </select>

          <select value={direction} onChange={(e) => setDirection(e.target.value)}>
            <option value="desc">내림차순</option>
            <option value="asc">오름차순</option>
          </select>
        </div>

        <div style={{ display: "flex", gap: "10px", flexWrap: "wrap" }}>
          <select value={searchType} onChange={(e) => setSearchType(e.target.value)}>
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
          />
          <button onClick={handleSearch}>검색</button>
        </div>
      </div>

      <table style={{ width: "100%", borderCollapse: "collapse", marginBottom: "20px" }}>
        <thead>
          <tr style={{ backgroundColor: "#f5f5f5", textAlign: "left" }}>
            <th style={{ padding: "8px", border: "1px solid #ccc" }}>번호</th>
            <th style={{ padding: "8px", border: "1px solid #ccc" }}>카테고리</th>
            <th style={{ padding: "8px", border: "1px solid #ccc" }}>제목</th>
            <th style={{ padding: "8px", border: "1px solid #ccc" }}>작성자</th>
            <th style={{ padding: "8px", border: "1px solid #ccc" }}>작성일</th>
            <th style={{ padding: "8px", border: "1px solid #ccc" }}>조회수</th>
            <th style={{ padding: "8px", border: "1px solid #ccc" }}>추천수</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((post) => (
            <tr key={post.postNo}>
              <td style={{ padding: "8px", border: "1px solid #ccc" }}>{post.postNo}</td>
              <td style={{ padding: "8px", border: "1px solid #ccc" }}>{post.category}</td>
              <td style={{ padding: "8px", border: "1px solid #ccc" }}>
  <Link to={`/boards/${post.postNo}`} style={{ color: "#007bff", textDecoration: "none" }}>
    {post.title}
  </Link>
</td>
              <td style={{ padding: "8px", border: "1px solid #ccc" }}>{post.nickname}</td>
              <td style={{ padding: "8px", border: "1px solid #ccc" }}>
  {new Date(post.updatedAt ?? post.createdAt).toLocaleString()}
  {post.updatedAt && " (수정됨)"}
</td>
              <td style={{ padding: "8px", border: "1px solid #ccc" }}>{post.views}</td>
              <td style={{ padding: "8px", border: "1px solid #ccc" }}>{post.likes}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <div style={{ display: "flex", gap: "6px", flexWrap: "wrap" }}>
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
        </div>
<button
  style={{ padding: "8px 16px", backgroundColor: "#007bff", color: "white", border: "none", borderRadius: "4px", cursor: "pointer" }}
  onClick={() => navigate(category === "ALL" ? "/boards/write" : `/boards/write?category=${category}`)}
>
  글쓰기
</button>
      </div>
    </div>
  );
}
