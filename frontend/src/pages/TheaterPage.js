import { useEffect, useState } from "react"; 
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "../error/redux/authSlice";
import axios from "../error/api/interceptor";
import TheaterMap from "../components/TheaterMap";
import { Link, useLocation, useNavigate } from "react-router-dom";
import WritePartyModal from "../components/WritePartyModal";
import LoginRequiredModal from "../components/LoginRequiredModal";
import { ErrorModal } from "../error/components/ErrorModal";
import dayjs from "dayjs";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import TextField from "@mui/material/TextField";
import "./css/TheaterPage.css";

const formatDate = (date) => dayjs(date).format("YYYY-MM-DDTHH:mm");

const groupTheatersByBrand = (theaters) => {
  const grouped = { CGV: [], "롯데시네마": [], "메가박스": [], 기타: [] };
  theaters.forEach((theater) => {
    if (theater.name.includes("CGV")) grouped.CGV.push(theater);
    else if (theater.name.includes("롯데시네마")) grouped["롯데시네마"].push(theater);
    else if (theater.name.includes("메가박스")) grouped["메가박스"].push(theater);
    else grouped["기타"].push(theater);
  });
  return grouped;
};

const getVisibleTheaters = (list, isExpanded) => isExpanded ? list : list.slice(0, 3);

const TheaterPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loginModalOpen, setLoginModalOpen] = useState(false);
  const [theaters, setTheaters] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [filteredTheaters, setFilteredTheaters] = useState([]);
  const [postCounts, setPostCounts] = useState({});
  const [searchStartDate, setSearchStartDate] = useState(dayjs());
  const [searchEndDate, setSearchEndDate] = useState(dayjs().add(14, "day"));
  const [searchMovie, setSearchMovie] = useState("");
  const [partyPosts, setPartyPosts] = useState([]);
  const [isExpanded, setIsExpanded] = useState(false);
  const [errorModalOpen, setErrorModalOpen] = useState(false);
  const [errorModalMessage, setErrorModalMessage] = useState("");

  const openErrorModal = (message) => {
    setErrorModalMessage(message);
    setErrorModalOpen(true);
  };

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const ids = params.getAll("ids").map(Number);
    const start = params.get("start");
    const end = params.get("end");
    const movie = params.get("movie") || "";

    const hasQuery = start || end || ids.length > 0 || movie;

    if (!hasQuery) {
      const defaultStart = dayjs().add(30, "minute");
      const defaultEnd = dayjs().add(14, "day");

      const defaultParams = new URLSearchParams();
      defaultParams.append("start", formatDate(defaultStart));
      defaultParams.append("end", formatDate(defaultEnd));

      navigate({ pathname: "/theaters", search: defaultParams.toString() });
      return;
    }

    setSearchStartDate(start ? dayjs(start) : dayjs().add(30, "minute"));
    setSearchEndDate(end ? dayjs(end) : dayjs().add(14, "day"));
    setSearchMovie(movie);
    setSelectedIds(ids);

    const fetchPosts = async () => {
      try {
        const query = new URLSearchParams(location.search).toString();
        const res = await axios.get(`/partyposts/theaters?${query}`);
        setPartyPosts(res.data);
      } catch {
        openErrorModal("모집글을 불러오지 못했습니다.");
        setPartyPosts([]);
      }
    };

    fetchPosts();
  }, [location.search, navigate]);

  useEffect(() => {
    axios.get("/theaters")
      .then((res) => {
        setTheaters(res.data);
        setFilteredTheaters(res.data);
      })
      .catch(() => openErrorModal("영화관 목록을 불러오지 못했습니다."));

    axios.get("/partyposts/theaters/count")
      .then((res) => setPostCounts(res.data))
      .catch(() => openErrorModal("모집글 수를 불러오지 못했습니다."));
  }, []);

  useEffect(() => {
    if (selectedIds.length === 0) {
      setFilteredTheaters(theaters);
    } else {
      const query = selectedIds.join(",");
      axios.get(`/theaters?ids=${query}`)
        .then((res) => setFilteredTheaters(res.data))
        .catch(() => openErrorModal("선택한 영화관 정보를 불러오지 못했습니다."));
    }
  }, [selectedIds, theaters]);

  const toggleTheater = (id) => {
    const newSelected = selectedIds.includes(id)
      ? selectedIds.filter((v) => v !== id)
      : [...selectedIds, id];

    setSelectedIds(newSelected);

    const params = new URLSearchParams();
    newSelected.forEach((id) => params.append("ids", id));
    if (searchStartDate) params.append("start", formatDate(searchStartDate));
    if (searchEndDate) params.append("end", formatDate(searchEndDate));
    if (searchMovie) params.append("movie", searchMovie);

    navigate({ pathname: "/theaters", search: params.toString() });
  };

  const selectAll = () => {
    setSelectedIds([]);
    const params = new URLSearchParams();
    if (searchStartDate) params.append("start", formatDate(searchStartDate));
    if (searchEndDate) params.append("end", formatDate(searchEndDate));
    if (searchMovie) params.append("movie", searchMovie);
    navigate({ pathname: "/theaters", search: params.toString() });
  };

  const handleSearchClick = () => {
    const params = new URLSearchParams();
    if (selectedIds.length > 0) selectedIds.forEach(id => params.append("ids", id));
    if (searchStartDate) params.append("start", formatDate(searchStartDate));
    if (searchEndDate) params.append("end", formatDate(searchEndDate));
    if (searchMovie) params.append("movie", searchMovie);
    navigate({ pathname: "/theaters", search: params.toString() });
  };

  const handleWriteClick = () => {
    if (!isAuthenticated) {
      setLoginModalOpen(true);
      return;
    }
    setIsModalOpen(true);
  };

  const grouped = groupTheatersByBrand(theaters);
  const brandLinks = {
    CGV: "https://www.cgv.co.kr/",
    "메가박스": "https://www.megabox.co.kr/",
    "롯데시네마": "https://www.lottecinema.co.kr/",
    기타: null,
  };

  return (
    <div className="theater-container">
      <div className="theater-buttons">
        <div className="left-buttons">
          <button onClick={selectAll}>전체</button>
        </div>
        <div className="right-buttons">
          {theaters.length > 5 && (
            <button onClick={() => setIsExpanded(!isExpanded)}>
              {isExpanded ? "▲ 접기" : "▼ 더보기"}
            </button>
          )}
        </div>
      </div>

      <div className="theater-brand-columns">
        {Object.entries(grouped).map(([brand, list]) => (
          <div key={brand} className="theater-column">
            <h4 className="brand-title">
              {brandLinks[brand] ? (
                <a
                  href={brandLinks[brand]}
                  target="_blank"
                  rel="noopener noreferrer"
                  className={brand.toLowerCase()}
                >
                  <img
                    src={`/images/logo-${brand === "롯데시네마" ? "lotte" : brand === "메가박스" ? "megabox" : "cgv"}.png`}
                    alt={brand}
                    className="brand-logo"
                  />
                </a>
              ) : (
                <span className="brand-text">[{brand}]</span>
              )}
            </h4>
            {getVisibleTheaters(list, isExpanded).map((theater) => (
              <label key={theater.theaterId} className="theater-item">
                <input
                  type="checkbox"
                  checked={selectedIds.includes(theater.theaterId)}
                  onChange={() => toggleTheater(theater.theaterId)}
                />
                {theater.name} ({postCounts[theater.theaterId] ?? 0}건)
              </label>
            ))}
          </div>
        ))}
      </div>

      <TheaterMap theaterList={filteredTheaters} />

      <div className="theater-controls">
        <div className="search-group">
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DateTimePicker
              label="시작일시"
              value={searchStartDate}
              onChange={(newValue) => setSearchStartDate(newValue)}
              minDateTime={dayjs().add(25, "minute")}
              format="YYYY-MM-DD HH:mm"
              renderInput={(params) => (
                <TextField {...params} size="small" className="custom-datepicker" />
              )}
            />
            <DateTimePicker
              label="종료일시"
              value={searchEndDate}
              onChange={(newValue) => setSearchEndDate(newValue)}
              minDateTime={searchStartDate}
              format="YYYY-MM-DD HH:mm"
              renderInput={(params) => (
                <TextField {...params} size="small" className="custom-datepicker" />
              )}
            />
          </LocalizationProvider>
          <TextField
            label="영화명"
            value={searchMovie}
            onChange={(e) => setSearchMovie(e.target.value)}
            placeholder="검색할 영화명 입력"
            size="small"
            className="search-movie-input"
          />
          <button onClick={handleSearchClick}>검색</button>
        </div>
        <button onClick={handleWriteClick}>글쓰기</button>
      </div>

      <h3>📋 모집글 목록</h3>
      {partyPosts.length === 0 ? (
        <p>조건에 맞는 모집글이 없습니다.</p>
      ) : (
        <table className="party-table">
          <thead>
            <tr>
              <th>제목</th>
              <th>영화명</th>
              <th>일시</th>
              <th>영화관</th>
              <th>모집성별</th>
              <th>모집인원</th>
            </tr>
          </thead>
          <tbody>
            {partyPosts.map((post) => (
              <tr key={post.partyPostNo}>
                <td>
                  <Link to={`/theaters/${post.partyPostNo}`} className="party-title-link">
                    {post.title}
                  </Link>
                </td>
                <td>{post.movie}</td>
                <td>{new Date(post.partyDeadline).toLocaleString()}</td>
                <td>{post.theaterName}</td>
                <td>{post.gender || "무관"}</td>
                <td>{post.partyLimit}명</td>
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
          axios.get(`/partyposts/theaters?${params.toString()}`)
            .then((res) => setPartyPosts(res.data))
            .catch(() => openErrorModal("모집글을 다시 불러오는 데 실패했습니다."));

          axios.get("/partyposts/theaters/count")
            .then((res) => setPostCounts(res.data))
            .catch(() => openErrorModal("모집글 수를 다시 불러오는 데 실패했습니다."));
        }}
      />

      <LoginRequiredModal isOpen={loginModalOpen} onClose={() => setLoginModalOpen(false)} />

      <ErrorModal
        isOpen={errorModalOpen}
        title="오류"
        message={errorModalMessage}
        onConfirm={() => setErrorModalOpen(false)}
        onCancel={() => setErrorModalOpen(false)}
      />
    </div>
  );
};

export default TheaterPage;