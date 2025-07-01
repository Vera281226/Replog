import { useEffect, useState } from "react";
import axios from "axios";
import TheaterMap from "../components/TheaterMap";
import { Link, useLocation, useNavigate } from "react-router-dom";
import WritePartyModal from "../components/WritePartyModal";

const formatDate = (date) => date.toISOString().slice(0, 10);

const TheaterPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [theaters, setTheaters] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [filteredTheaters, setFilteredTheaters] = useState([]);
  const [postCounts, setPostCounts] = useState({});

  const [searchStartDate, setSearchStartDate] = useState("");
  const [searchEndDate, setSearchEndDate] = useState("");
  const [searchMovie, setSearchMovie] = useState("");

  const [partyPosts, setPartyPosts] = useState([]);
  const [isExpanded, setIsExpanded] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const ids = params.getAll("ids").map(Number);
    const start = params.get("start");
    const end = params.get("end");
    const movie = params.get("movie") || "";

    const today = new Date();
    const twoWeeksLater = new Date();
    twoWeeksLater.setDate(today.getDate() + 14);
    const defaultStart = formatDate(today);
    const defaultEnd = formatDate(twoWeeksLater);

    const appliedStart = start || defaultStart;
    const appliedEnd = end || defaultEnd;

    setSearchStartDate(appliedStart);
    setSearchEndDate(appliedEnd);
    setSearchMovie(movie);
    setSelectedIds(ids);

    const fetchPosts = async () => {
      const query = new URLSearchParams(location.search).toString();
      try {
        const res = await axios.get(`/api/partyposts/theaters?${query}`);
        setPartyPosts(res.data);
      } catch (err) {
        console.error("뒤로가기 후 모집글 다시 불러오기 실패", err);
        setPartyPosts([]);
      }
    };

    fetchPosts();
  }, [location.search]);

  useEffect(() => {
    axios.get("/api/theaters")
      .then((res) => {
        setTheaters(res.data);
        setFilteredTheaters(res.data);
      });

    axios.get("/api/partyposts/theaters/count")
      .then((res) => setPostCounts(res.data))
      .catch((err) => console.error("모집글 개수 불러오기 실패", err));
  }, []);

  useEffect(() => {
    if (selectedIds.length === 0) {
      setFilteredTheaters(theaters);
    } else {
      const query = selectedIds.join(",");
      axios.get(`/api/theaters?ids=${query}`)
        .then((res) => setFilteredTheaters(res.data))
        .catch((err) => console.error("선택 영화관 조회 실패", err));
    }
  }, [selectedIds, theaters]);

  const toggleTheater = (id) => {
    let newSelected;
    if (selectedIds.includes(id)) {
      newSelected = selectedIds.filter((v) => v !== id);
    } else {
      newSelected = [...selectedIds, id];
    }
    setSelectedIds(newSelected);

    const params = new URLSearchParams();
    newSelected.forEach((id) => params.append("ids", id));
    if (searchStartDate) params.append("start", searchStartDate);
    if (searchEndDate) params.append("end", searchEndDate);
    if (searchMovie) params.append("movie", searchMovie);

    navigate({ pathname: "/theaters", search: params.toString() });
  };

  const selectAll = () => {
    setSelectedIds([]);
    const params = new URLSearchParams();
    if (searchStartDate) params.append("start", searchStartDate);
    if (searchEndDate) params.append("end", searchEndDate);
    if (searchMovie) params.append("movie", searchMovie);
    navigate({ pathname: "/theaters", search: params.toString() });
  };

  const handleSearchClick = () => {
    const params = new URLSearchParams();
    if (selectedIds.length > 0) selectedIds.forEach(id => params.append("ids", id));
    if (searchStartDate) params.append("start", searchStartDate);
    if (searchEndDate) params.append("end", searchEndDate);
    if (searchMovie) params.append("movie", searchMovie);

    navigate({ pathname: "/theaters", search: params.toString() });
  };

  const visibleTheaters = isExpanded ? theaters : theaters.slice(0, 5);

  return (
    <div style={{ width: "1200px", margin: "0 auto", padding: "20px" }}>
      <h2 style={{ display: "flex", alignItems: "center", gap: "10px" }}>
        🎬 영화관 선택
        <a href="https://www.cgv.co.kr/" target="_blank" rel="noopener noreferrer" style={{ textDecoration: "none", color: "#E71A0F" }}>CGV</a>
        <a href="https://www.megabox.co.kr/" target="_blank" rel="noopener noreferrer" style={{ textDecoration: "none", color: "#503396" }}>메가박스</a>
        <a href="https://www.lottecinema.co.kr/" target="_blank" rel="noopener noreferrer" style={{ textDecoration: "none", color: "#8B1E1D" }}>롯데시네마</a>
      </h2>

      <div style={{ marginBottom: "10px", display: "flex", justifyContent: "space-between" }}>
        <button onClick={selectAll}>전체</button>
        {theaters.length > 5 && (
          <button onClick={() => setIsExpanded(!isExpanded)}>
            {isExpanded ? "▲ 접기" : "▼ 더보기"}
          </button>
        )}
      </div>

      <div style={{ display: "grid", gridTemplateColumns: "repeat(5, 1fr)", gap: "10px", marginBottom: "20px" }}>
        {visibleTheaters.map((theater) => (
          <label key={theater.theaterId} style={{ cursor: "pointer" }}>
            <input
              type="checkbox"
              checked={selectedIds.includes(theater.theaterId)}
              onChange={() => toggleTheater(theater.theaterId)}
            />
            {theater.name} ({postCounts[theater.theaterId] ?? 0}건)
          </label>
        ))}
      </div>

      <TheaterMap theaterList={filteredTheaters} />

      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", margin: "20px 0" }}>
        <div style={{ display: "flex", gap: "10px", alignItems: "center" }}>
          <label>
            시작일: <input type="date" value={searchStartDate} onChange={(e) => setSearchStartDate(e.target.value)} />
          </label>
          <label>
            종료일: <input type="date" value={searchEndDate} onChange={(e) => setSearchEndDate(e.target.value)} />
          </label>
          <label>
            영화명: <input type="text" value={searchMovie} onChange={(e) => setSearchMovie(e.target.value)} placeholder="검색할 영화명 입력" />
          </label>
          <button onClick={handleSearchClick}>검색</button>
        </div>
        <button onClick={() => setIsModalOpen(true)}>글쓰기</button>
      </div>

      <h3>📋 모집글 목록</h3>
      {partyPosts.length === 0 ? (
        <p>조건에 맞는 모집글이 없습니다.</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr>
              <th style={th}>제목</th>
              <th style={th}>영화명</th>
              <th style={th}>일시</th>
              <th style={th}>영화관</th>
              <th style={th}>모집성별</th>
              <th style={th}>모집인원</th>
            </tr>
          </thead>
          <tbody>
            {partyPosts.map((post) => (
              <tr key={post.partyPostNo}>
                <td style={td}>
                  <Link to={`/theaters/${post.partyPostNo}`} style={{ textDecoration: "none", color: "blue" }}>
                    {post.title}
                  </Link>
                </td>
                <td style={td}>{post.movie}</td>
                <td style={td}>{new Date(post.partyDeadline).toLocaleString()}</td>
                <td style={td}>{post.theaterName}</td>
                <td style={td}>{post.gender || "무관"}</td>
                <td style={td}>{post.partyLimit}명</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <WritePartyModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmitSuccess={() => {
          const params = new URLSearchParams(location.search);
          axios.get(`/api/partyposts/theaters?${params.toString()}`)
            .then((res) => setPartyPosts(res.data))
            .catch((err) => console.error("모집글 다시 불러오기 실패", err));
        }}
      />
    </div>
  );
};

const th = {
  borderBottom: "1px solid #ccc",
  padding: "8px",
  textAlign: "left",
};

const td = {
  borderBottom: "1px solid #eee",
  padding: "8px",
};

export default TheaterPage;
