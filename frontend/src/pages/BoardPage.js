import { useEffect, useState, useCallback } from "react"; 
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "../error/redux/authSlice";
import axios from "../error/api/interceptor";
import { Link, useNavigate, useLocation } from "react-router-dom";
import LoginRequiredModal from "../components/LoginRequiredModel";
import { ErrorModal } from "../error/components/ErrorModal"; // ✅ 추가
import "./css/BoardPage.css";

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

export default function BoardPage() {
  const navigate = useNavigate();
  const query = useQuery();

  const isAuthenticated = useSelector(selectIsAuthenticated);
  const initialCategory = query.get("category") || "ALL";

  const [posts, setPosts] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [category, setCategory] = useState(initialCategory);
  const [sortBy, setSortBy] = useState("createdAt");
  const [direction, setDirection] = useState("desc");
  const [searchType, setSearchType] = useState("title");
  const [searchKeyword, setSearchKeyword] = useState("");
  const [loginModalOpen, setLoginModalOpen] = useState(false);
  const [lastSearchParams, setLastSearchParams] = useState({
    searchType: "title",
    searchKeyword: ""
  });

  // ✅ 에러 모달 상태
  const [errorModalOpen, setErrorModalOpen] = useState(false);
  const [errorModalMessage, setErrorModalMessage] = useState("");

  const openErrorModal = (message) => {
    setErrorModalMessage(message);
    setErrorModalOpen(true);
  };

  const fetchPosts = useCallback(async () => {
    try {
      const params = {
        page,
        sortBy,
        direction,
        ...(category !== "ALL" && { category }),
        ...(lastSearchParams.searchKeyword && {
          searchType: lastSearchParams.searchType,
          searchKeyword: lastSearchParams.searchKeyword
        })
      };
      const res = await axios.get("/posts/filter", { params });
      setPosts(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      const message = err.response?.data?.message || "게시글 목록을 불러오는 데 실패했습니다.";
      openErrorModal(message);
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

  const handleWriteClick = () => {
    if (!isAuthenticated) {
      setLoginModalOpen(true);
      return;
    }
    const target =
      category === "ALL"
        ? "/boards/write"
        : `/boards/write?category=${encodeURIComponent(category)}`;
    navigate(target);
  };

  const formatDate = (dateString) => {
    const now = new Date();
    const date = new Date(dateString);
    const isToday = now.toDateString() === date.toDateString();

    if (isToday) {
      const hours = date.getHours().toString().padStart(2, "0");
      const minutes = date.getMinutes().toString().padStart(2, "0");
      return `${hours}:${minutes}`;
    } else {
      const year = String(date.getFullYear()).slice(2);
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}.${month}.${day}`;
    }
  };

  return (
    <div className="board-page">
      <div className="board-controls">
        <div className="board-sort">
          <select
            value={`${sortBy}_${direction}`}
            onChange={(e) => {
              const [sort, dir] = e.target.value.split("_");
              setSortBy(sort);
              setDirection(dir);
            }}
          >
            <option value="createdAt_desc">최신순</option>
            <option value="views_desc">조회순</option>
            <option value="likes_desc">추천순</option>
          </select>
        </div>

        <div className="board-categories">
          {["ALL", "자유게시판", "스포", "공지사항", "개봉예정작"].map((cat) => (
            <button
              key={cat}
              onClick={() => {
                setCategory(cat);
                setPage(1);
                navigate(`/boards?category=${encodeURIComponent(cat)}`);
              }}
              className={`category-button ${category === cat ? "active" : ""}`}
            >
              {cat === "ALL" ? "전체" : cat}
            </button>
          ))}
        </div>

        <div className="board-search">
          <select
            value={searchType}
            onChange={(e) => setSearchType(e.target.value)}
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
          />
          <button onClick={handleSearch}>검색</button>
        </div>
      </div>

      <table className="board-table">
        <thead>
          <tr>
            <th>#</th>
            <th>카테고리</th>
            <th>제목</th>
            <th>닉네임</th>
            <th>날짜</th>
            <th>조회</th>
            <th>추천</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((post) => (
            <tr key={post.postNo}>
              <td>{post.postNo}</td>
              <td>{post.category}</td>
              <td>
                <Link to={`/boards/${post.postNo}`}>{post.title}</Link>
              </td>
              <td>{post.nickname}</td>
              <td>{formatDate(post.createdAt)}</td>
              <td>{post.views}</td>
              <td>{post.likes}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="board-footer">
        <div className="pagination">
          <button disabled={page === 1} onClick={() => setPage(page - 1)}>
            {"<"}
          </button>
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i + 1}
              onClick={() => setPage(i + 1)}
              className={page === i + 1 ? "active" : ""}
            >
              {i + 1}
            </button>
          ))}
          <button
            disabled={page === totalPages}
            onClick={() => setPage(page + 1)}
          >
            {">"}
          </button>
        </div>
        <button className="write-button" onClick={handleWriteClick}>
          글쓰기
        </button>
      </div>

      <LoginRequiredModal isOpen={loginModalOpen} />

      {/* 에러 모달 */}
      <ErrorModal
        isOpen={errorModalOpen}
        title="오류"
        message={errorModalMessage}
        onConfirm={() => setErrorModalOpen(false)}
        onCancel={() => setErrorModalOpen(false)}
      />
    </div>
  );
}