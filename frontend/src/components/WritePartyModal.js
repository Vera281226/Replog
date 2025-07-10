import { useState, useEffect } from "react";
import axios from "../error/api/interceptor";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Underline from "@tiptap/extension-underline";
import Link from "@tiptap/extension-link";
import TextAlign from "@tiptap/extension-text-align";
import Toolbar from "../components/TiptapToolbar";
import { ErrorModal } from "../error/components/ErrorModal";
import CustomDatePicker from "../components/CustomDatePicker";
import dayjs from "dayjs";
import "./WritePartyModal.css";

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
    partyDeadline: dayjs().add(1, 'hour').format("YYYY-MM-DDTHH:mm"),
    theaterId: "",
    partyLimit: 2,
    gender: "",
    ageGroupsMask: 0,
  });

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
      TextAlign.configure({ types: ["heading", "paragraph"] }),
    ],
    content: "",
    onUpdate: ({ editor }) => {
      setForm((prev) => ({ ...prev, content: editor.getHTML() }));
    },
  });

  useEffect(() => {
  if (editor) {
    editor.commands.setContent([
      {
        type: "paragraph",
        content: [{ type: "text", text: "내용: " }],
      },
      {
        type: "paragraph",
        content: [{ type: "text", text: "모집위치: " }],
      },
      {
        type: "paragraph",
        content: [{ type: "text", text: "애프터 여부: " }],
      },
    ]);
  }
}, [editor]);

  useEffect(() => {
    if (!isOpen) return;

    axios
      .get("/theaters")
      .then((res) => {
        setTheaters(res.data);
        setFilteredTheaters(res.data);
      })
      .catch(() => {
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
    const updatedForm = { ...form, content: editor?.getHTML() || "" };

    axios
      .post("/partyposts", updatedForm, { withCredentials: true })
      .then(() => {
        onClose();
        if (onSubmitSuccess) onSubmitSuccess();

        setForm({
          movie: "",
          title: "",
          content: "",
          partyDeadline: dayjs().add(1, 'hour').format("YYYY-MM-DDTHH:mm"),
          theaterId: "",
          partyLimit: 2,
          gender: "",
          ageGroupsMask: 0,
        });
        setSelectedAges([]);
        editor?.commands.setContent("");
      })
      .catch((err) => {
        const message =
          err.response?.data?.message ||
          "알 수 없는 오류로 등록에 실패했습니다.";
        openErrorModal(message);
      });
  };

  const selectedTheaterName =
    theaters.find((t) => t.theaterId === Number(form.theaterId))?.name || "";

  const handleSearch = () => {
    const keyword = searchKeyword.toLowerCase();
    setFilteredTheaters(
      theaters.filter((t) => t.name.toLowerCase().includes(keyword))
    );
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close-button" onClick={onClose}>
          ×
        </button>

        <h2>🎬 모집글 작성</h2>
        <form onSubmit={handleSubmit} className="write-form">
          <input
            name="movie"
            value={form.movie}
            onChange={handleChange}
            placeholder="영화 제목"
            required
          />
          <input
            name="title"
            value={form.title}
            onChange={handleChange}
            placeholder="모집글 제목"
            required
          />

          <div className="editor-section">
            <Toolbar editor={editor} />
            <div
              className="editor-wrapper"
              onClick={() => editor?.commands.focus()}
            >
              <EditorContent editor={editor} className="tiptap" />
            </div>
          </div>

          <CustomDatePicker
            label="모집 마감일시"
            date={form.partyDeadline}
            setDate={(val) => setForm({ ...form, partyDeadline: val })}
            min={dayjs().format("YYYY-MM-DDTHH:mm")}
          />

          <div className="theater-input-wrapper">
            <input
              type="text"
              value={selectedTheaterName}
              placeholder="영화관을 선택하세요"
              readOnly
              onClick={() => setIsTheaterModalOpen(true)}
              className="theater-select-input"
            />
            <span className="theater-icon" role="img" aria-label="movie">
              🎬
            </span>
          </div>

          <div className="party-limit-wrapper">
            <label>
              인원:
              <input
                type="number"
                name="partyLimit"
                min="2"
                max="9"
                value={form.partyLimit}
                onChange={handleChange}
                required
              />
            </label>
          </div>

          <div className="gender-options">
            성별 조건:
            <label>
              <input
                type="radio"
                name="gender"
                value=""
                onChange={handleChange}
                checked={form.gender === ""}
              /> 무관
            </label>
            <label>
              <input
                type="radio"
                name="gender"
                value="남"
                onChange={handleChange}
                checked={form.gender === "남"}
              /> 남
            </label>
            <label>
              <input
                type="radio"
                name="gender"
                value="여"
                onChange={handleChange}
                checked={form.gender === "여"}
              /> 여
            </label>
          </div>

          <label>연령대 조건 (복수 선택 가능):</label>
          <div className="age-group-checkboxes">
            {ageGroups.map(({ label, value }) => (
              <label key={value}>
                <input
                  type="checkbox"
                  checked={selectedAges.includes(value)}
                  onChange={() => toggleAgeGroup(value)}
                />
                {label}
              </label>
            ))}
          </div>

          <input type="hidden" name="ageGroupsMask" value={form.ageGroupsMask} />
          <button type="submit">등록하기</button>
        </form>

        {isTheaterModalOpen && (
          <div className="sub-modal" onClick={() => setIsTheaterModalOpen(false)}>
            <div className="sub-modal-content" onClick={(e) => e.stopPropagation()}>
              <button
                className="modal-close-button"
                onClick={() => setIsTheaterModalOpen(false)}
              >
                ×
              </button>
              <div className="sub-search-bar">
                <input
                  type="text"
                  placeholder="영화관 이름 검색"
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                />
                <button type="button" onClick={handleSearch}>
                  검색
                </button>
              </div>
              {filteredTheaters.map((theater) => (
                <div
                  key={theater.theaterId}
                  className="theater-option"
                  onClick={() => {
                    setForm({ ...form, theaterId: theater.theaterId });
                    setIsTheaterModalOpen(false);
                  }}
                >
                  {theater.name}
                </div>
              ))}
            </div>
          </div>
        )}

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
