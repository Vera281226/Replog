import { useState, useEffect } from "react";
import axios from "../error/api/interceptor";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Underline from "@tiptap/extension-underline";
import Link from "@tiptap/extension-link";
import TextAlign from "@tiptap/extension-text-align";
import Toolbar from "../components/TiptapToolbar";
import { ErrorModal } from "../error/components/ErrorModal"; // ✅ 모달 import

const ageGroups = [
  { label: "10대", value: 0 },
  { label: "20대", value: 1 },
  { label: "30대", value: 2 },
  { label: "40대", value: 3 },
  { label: "50대", value: 4 },
  { label: "60대", value: 5 },
];

const WritePartyModal = ({ isOpen, onClose, onSubmitSuccess }) => {
  const [selectedAges, setSelectedAges] = useState([]);
  const [theaters, setTheaters] = useState([]);
  const [filteredTheaters, setFilteredTheaters] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [isTheaterModalOpen, setIsTheaterModalOpen] = useState(false);

  const [form, setForm] = useState({
    movie: "",
    title: "",
    content: "",
    partyDeadline: "",
    theaterId: "",
    partyLimit: 2,
    gender: "",
    ageGroupsMask: 0,
  });

  // ✅ 에러 모달 상태
  const [errorModalOpen, setErrorModalOpen] = useState(false);
  const [errorModalMessage, setErrorModalMessage] = useState("");

  const openErrorModal = (message) => {
    setErrorModalMessage(message);
    setErrorModalOpen(true);
  };

  const editor = useEditor({
    extensions: [
      StarterKit,
      Underline,
      Link,
      TextAlign.configure({ types: ['heading', 'paragraph'] })
    ],
    content: form.content,
    onUpdate: ({ editor }) => {
      setForm((prev) => ({ ...prev, content: editor.getHTML() }));
    },
  });

  useEffect(() => {
    if (!isOpen) return;

    axios.get("/theaters")
      .then((res) => {
        setTheaters(res.data);
        setFilteredTheaters(res.data);
      })
      .catch((err) => {
        openErrorModal("영화관 목록을 불러오지 못했습니다.");
      });
  }, [isOpen]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const toggleAgeGroup = (bit) => {
    const updated = selectedAges.includes(bit)
      ? selectedAges.filter((v) => v !== bit)
      : [...selectedAges, bit];

    const mask = updated.reduce((acc, cur) => acc | (1 << cur), 0);
    setSelectedAges(updated);
    setForm({ ...form, ageGroupsMask: mask });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const updatedForm = {
      ...form,
      content: editor?.getHTML() || "",
    };

    axios.post("/partyposts", updatedForm, { withCredentials: true })
      .then(() => {
        onClose();
        if (onSubmitSuccess) onSubmitSuccess();

        setForm({
          movie: "",
          title: "",
          content: "",
          partyDeadline: "",
          theaterId: "",
          partyLimit: 2,
          gender: "",
          ageGroupsMask: 0,
        });
        setSelectedAges([]);
        editor?.commands.setContent("");
      })
      .catch((err) => {
        const message = err.response?.data?.message || "알 수 없는 오류로 등록에 실패했습니다.";
        openErrorModal(message);
      });
  };

  const selectedTheaterName = theaters.find(t => t.theaterId === Number(form.theaterId))?.name || "";

  const handleSearch = () => {
    const keyword = searchKeyword.toLowerCase();
    setFilteredTheaters(theaters.filter(t => t.name.toLowerCase().includes(keyword)));
  };

  if (!isOpen) return null;

  return (
    <div style={{
      position: "fixed", top: 0, left: 0, right: 0, bottom: 0,
      backgroundColor: "rgba(0,0,0,0.5)", display: "flex",
      justifyContent: "center", alignItems: "center", zIndex: 1000
    }} onClick={onClose}>
      <div
        style={{ background: "#fff", padding: "20px", width: "800px", maxHeight: "90vh", overflowY: "auto", position: "relative" }}
        onClick={(e) => e.stopPropagation()}
      >
        <button
          style={{ position: "absolute", top: "10px", right: "10px", fontSize: "20px", background: "none", border: "none", cursor: "pointer" }}
          onClick={onClose}
        >
          ×
        </button>

        <h2>🎬 모집글 작성</h2>
        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
          <input name="movie" value={form.movie} onChange={handleChange} placeholder="영화 제목" required />
          <input name="title" value={form.title} onChange={handleChange} placeholder="모집글 제목" required />

          <div style={{ display: "flex", alignItems: "flex-start", gap: "10px" }}>
            <div style={{ flex: 1 }}>
              <Toolbar editor={editor} />
                <div className="editor-wrapper" onClick={() => editor?.commands.focus()}>
                <EditorContent editor={editor} className="tiptap" />
              </div>
            </div>
          </div>

          <input
            type="datetime-local"
            name="partyDeadline"
            value={form.partyDeadline}
            onChange={handleChange}
            min={new Date().toISOString().slice(0, 16)}
            required
          />

          <div style={{ display: "flex", alignItems: "center", position: "relative" }}>
            <input
              type="text"
              value={selectedTheaterName}
              placeholder="영화관을 선택하세요"
              readOnly
              onClick={() => setIsTheaterModalOpen(true)}
              style={{ flex: 1, padding: "10px 30px 10px 10px", cursor: "pointer" }}
            />
            <span role="img" aria-label="movie" style={{ position: "absolute", right: "10px", fontSize: "18px", color: "#666" }}>🎬</span>
          </div>

          <label>
            모집 인원 (최대 9명):
            <input type="number" name="partyLimit" min="2" max="9" value={form.partyLimit} onChange={handleChange} required />
          </label>

          <div>
            성별 조건:
            <label><input type="radio" name="gender" value="" onChange={handleChange} checked={form.gender === ""} /> 무관</label>
            <label><input type="radio" name="gender" value="남" onChange={handleChange} checked={form.gender === "남"} /> 남</label>
            <label><input type="radio" name="gender" value="여" onChange={handleChange} checked={form.gender === "여"} /> 여</label>
          </div>

          <label>연령대 조건 (복수 선택 가능):</label>
          <div style={{ display: "flex", flexWrap: "wrap", gap: "10px" }}>
            {ageGroups.map(({ label, value }) => (
              <label key={value}>
                <input type="checkbox" checked={selectedAges.includes(value)} onChange={() => toggleAgeGroup(value)} />
                {label}
              </label>
            ))}
          </div>

          <input type="hidden" name="ageGroupsMask" value={form.ageGroupsMask} />
          <button type="submit">등록하기</button>
        </form>

        {/* 영화관 선택 모달 */}
        {isTheaterModalOpen && (
          <div style={{
            position: "fixed", top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: "rgba(0,0,0,0.5)", display: "flex",
            justifyContent: "center", alignItems: "center", zIndex: 1100
          }} onClick={() => setIsTheaterModalOpen(false)}>
            <div
              style={{ background: "#fff", padding: "20px", width: "400px", height: "500px", overflowY: "auto", position: "relative" }}
              onClick={(e) => e.stopPropagation()}
            >
              <button
                style={{ position: "absolute", top: "10px", right: "10px", background: "none", border: "none", fontSize: "20px", cursor: "pointer" }}
                onClick={() => setIsTheaterModalOpen(false)}
              >
                ×
              </button>
              <div style={{ display: "flex", gap: "8px", marginTop: "20px", marginBottom: "10px" }}>
                <input type="text" placeholder="영화관 이름 검색" value={searchKeyword} onChange={(e) => setSearchKeyword(e.target.value)} style={{ flex: 1 }} />
                <button type="button" onClick={handleSearch}>검색</button>
              </div>
              {filteredTheaters.map(theater => (
                <div key={theater.theaterId} style={{ padding: "8px", borderBottom: "1px solid #eee", cursor: "pointer" }} onClick={() => {
                  setForm({ ...form, theaterId: theater.theaterId });
                  setIsTheaterModalOpen(false);
                }}>
                  {theater.name}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 에러 모달 */}
        <ErrorModal
          isOpen={errorModalOpen}
          title="오류"
          message={errorModalMessage}
          onConfirm={() => setErrorModalOpen(false)}
          onCancel={() => setErrorModalOpen(false)}
        />
      </div>
    </div>
  );
};

export default WritePartyModal;