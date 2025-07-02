// src/pages/WritePostPage.js

import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useSelector } from "react-redux";
import axios from "axios";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Underline from "@tiptap/extension-underline";
import TextAlign from "@tiptap/extension-text-align";
import TiptapToolbar from "../components/TiptapToolbar";
import { selectCurrentUser } from "../error/redux/authSlice";
import "./WritePostPage.css"; // 버튼 스타일 등 통합 CSS

export default function WritePostPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const queryCategory = queryParams.get("category");

  // Redux에서 현재 사용자 정보 조회
  const currentUser = useSelector(selectCurrentUser);

  // Tiptap 에디터 초기화
  const editor = useEditor({
    extensions: [
      StarterKit,
      Underline,
      TextAlign.configure({ types: ["heading", "paragraph"] }),
    ],
    content: "",
  });

  // 폼 상태
  const [form, setForm] = useState({
    memberId: "",
    nickname: "",
    category: queryCategory || "",
    title: "",
  });

  // 로그인 정보가 바뀔 때마다 memberId, nickname 초기화
  useEffect(() => {
    setForm((prev) => ({
      ...prev,
      memberId: currentUser?.memberId || "testUser1",
      nickname: currentUser?.nickname || "테스트유저",
    }));
  }, [currentUser]);

  // 입력 핸들러
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  // 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.category) {
      alert("카테고리를 선택해주세요.");
      return;
    }
    if (!form.title.trim()) {
      alert("제목을 입력해주세요.");
      return;
    }
    const contentHtml = editor?.getHTML() || "";
    try {
      await axios.post(
        "/api/posts",
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
    } catch (err) {
      console.error(err);
      alert("글 작성에 실패했습니다.");
    }
  };

  return (
    <div className="write-post-page">
      <div className="write-post-container">
        <h2 className="write-post-title">글쓰기</h2>
        <form className="write-post-form" onSubmit={handleSubmit}>
          <select
            name="category"
            value={form.category}
            onChange={handleChange}
            required
            className="write-post-input"
          >
            <option value="" disabled hidden>
              카테고리를 선택하세요
            </option>
            <option value="자유게시판">자유게시판</option>
            <option value="스포">스포</option>
            <option value="공지사항">공지사항</option>
            <option value="개봉예정작">개봉예정작</option>
          </select>

          <input
            type="text"
            name="title"
            placeholder="제목"
            value={form.title}
            onChange={handleChange}
            required
            className="write-post-input"
          />

          <TiptapToolbar editor={editor} />
          <div className="editor-wrapper">
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
    </div>
  );
}
