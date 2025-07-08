import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";
import axios from "../error/api/interceptor";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Underline from "@tiptap/extension-underline";
import TextAlign from "@tiptap/extension-text-align";
import TiptapToolbar from "../components/TiptapToolbar";
import {
  selectCurrentUser,
  selectIsAuthenticated,
} from "../error/redux/authSlice";
import BannedWordFilterModal, {
  checkBannedWords,
} from "../components/BannedWordFilterModal";
import { ErrorModal } from "../error/components/ErrorModal"; // ✅ 추가
import "./css/WritePostPage.css";


export default function WritePostPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const queryCategory = queryParams.get("category");

  const currentUser = useSelector(selectCurrentUser);
  const isAuthenticated = useSelector(selectIsAuthenticated);

  const editor = useEditor({
    extensions: [
      StarterKit,
      Underline,
      TextAlign.configure({ types: ["heading", "paragraph"] }),
    ],
    content: "",
  });

  const [form, setForm] = useState({
    memberId: "",
    nickname: "",
    category: queryCategory || "",
    title: "",
  });

  const [bannedModalOpen, setBannedModalOpen] = useState(false);
  const [bannedWordsMatched, setBannedWordsMatched] = useState([]);
  const [errorModalOpen, setErrorModalOpen] = useState(false);
  const [errorModalMessage, setErrorModalMessage] = useState("");

  const [isAdmin, setIsAdmin] = useState(false); // ✅ 관리자 여부 상태

  const openErrorModal = (message) => {
    setErrorModalMessage(message);
    setErrorModalOpen(true);
  };

  useEffect(() => {
    if (!isAuthenticated) {
      openErrorModal("로그인이 필요한 기능입니다.");
      setTimeout(() => navigate("/login"), 1500);
      return;
    }

    setForm((prev) => ({
      ...prev,
      memberId: currentUser?.memberId || "",
      nickname: currentUser?.nickname || "",
    }));

    // ✅ 관리자 여부 확인
    const checkAdmin = async () => {
      try {
        await axios.get("/auth/admin-only");
        setIsAdmin(true);
      } catch {
        setIsAdmin(false);
      }
    };

    checkAdmin();
  }, [currentUser, isAuthenticated, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.category) {
      openErrorModal("카테고리를 선택해주세요.");
      return;
    }

    if (!form.title.trim()) {
      openErrorModal("제목을 입력해주세요.");
      return;
    }

    if (!editor?.getText().trim()) {
      openErrorModal("내용을 입력해주세요.");
      return;
    }

    const contentHtml = editor?.getHTML() || "";
    const fullText = `${form.title} ${editor?.getText() || ""}`;
    const { hasBanned, matchedWords } = checkBannedWords(fullText);

    if (hasBanned) {
      setBannedWordsMatched(matchedWords);
      setBannedModalOpen(true);
      return;
    }

    try {
      await axios.post(
        "/posts",
        {
          memberId: form.memberId,
          nickname: form.nickname,
          category: form.category,
          title: form.title,
          content: contentHtml,
        },
        { withCredentials: true }
      );
      navigate("/boards");
    } catch {
      openErrorModal("글 작성에 실패했습니다.");
    }
  };

  return (
    <div className="write-post-page">
      <div className="write-post-container">
        <h2 className="write-post-title">글쓰기</h2>
        <form className="write-post-form" onSubmit={handleSubmit}>
          <input
            type="text"
            name="nickname"
            value={form.nickname}
            readOnly
            className="write-post-input"
            placeholder="닉네임"
          />

          <select
            name="category"
            value={form.category}
            onChange={handleChange}
            className="write-post-input"
          >
            <option value="" disabled hidden>
              카테고리를 선택하세요
            </option>
            <option value="자유게시판">자유게시판</option>
            <option value="스포">스포</option>
            {isAdmin && <option value="공지사항">공지사항</option>}
            <option value="개봉예정작">개봉예정작</option>
          </select>

          <input
            type="text"
            name="title"
            placeholder="제목"
            value={form.title}
            onChange={handleChange}
            className="write-post-input"
          />

          <TiptapToolbar editor={editor} />
          <div
            className="editor-wrapper"
            onClick={() => editor?.commands.focus()}
          >
            <EditorContent editor={editor} />
          </div>

          <div className="button-group">
            <button type="submit" className="btn btn-primary">
              등록
            </button>
            <button
              type="button"
              onClick={() => navigate("/boards")}
              className="btn btn-secondary"
            >
              취소
            </button>
          </div>
        </form>
      </div>

      <BannedWordFilterModal
        isOpen={bannedModalOpen}
        matchedWords={bannedWordsMatched}
        onClose={() => setBannedModalOpen(false)}
      />

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