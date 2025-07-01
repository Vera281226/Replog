import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Underline from "@tiptap/extension-underline";
import TextAlign from "@tiptap/extension-text-align";
import TiptapToolbar from "../components/TiptapToolbar";

export default function PostCreatePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const queryCategory = queryParams.get("category");

  const [form, setForm] = useState({
    memberId: "",
    nickname: "",
    category: queryCategory || "",
    title: "",
  });

  const editor = useEditor({
    extensions: [
      StarterKit,
      Underline,
      TextAlign.configure({ types: ["heading", "paragraph"] }),
    ],
    content: "",
  });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.category) {
      alert("카테고리를 선택해주세요.");
      return;
    }
    try {
      await axios.post("/api/posts", {
        ...form,
        content: editor?.getHTML() || "",
      });
      navigate("/boards");
    } catch (err) {
      alert("글 작성에 실패했습니다.");
    }
  };

  return (
    <div style={{ padding: "40px 20px", maxWidth: "800px", margin: "0 auto" }}>
      <div
        style={{
          backgroundColor: "#ffffff",
          borderRadius: "12px",
          padding: "30px",
          boxShadow: "0 4px 20px rgba(0,0,0,0.1)",
        }}
      >
        <h2 style={{ marginBottom: "24px", fontSize: "24px", fontWeight: "bold", color: "#333" }}>글쓰기</h2>
        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
          <input
            type="text"
            name="memberId"
            placeholder="아이디"
            value={form.memberId}
            onChange={handleChange}
            required
            style={inputStyle}
          />
          <input
            type="text"
            name="nickname"
            placeholder="닉네임"
            value={form.nickname}
            onChange={handleChange}
            required
            style={inputStyle}
          />
          <select
            name="category"
            value={form.category}
            onChange={handleChange}
            required
            style={inputStyle}
          >
            <option value="" disabled hidden>카테고리를 선택하세요</option>
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
            style={inputStyle}
          />

          <TiptapToolbar editor={editor} />
          <div
            style={{
              border: "1px solid #ddd",
              borderRadius: "8px",
              padding: "12px",
              minHeight: "250px",
              backgroundColor: "#fafafa",
            }}
          >
            <EditorContent editor={editor} />
          </div>

          <div style={{ display: "flex", justifyContent: "space-between", gap: "12px" }}>
            <button
              type="submit"
              style={{
                flex: 1,
                padding: "12px",
                backgroundColor: "#007bff",
                color: "white",
                border: "none",
                borderRadius: "6px",
                fontWeight: "bold",
                cursor: "pointer",
                transition: "background-color 0.2s ease-in-out",
              }}
              onMouseOver={(e) => e.currentTarget.style.backgroundColor = "#0056b3"}
              onMouseOut={(e) => e.currentTarget.style.backgroundColor = "#007bff"}
            >
              등록
            </button>

            <button
              type="button"
              onClick={() => navigate("/boards")}
              style={{
                flex: 1,
                padding: "12px",
                backgroundColor: "#6c757d",
                color: "white",
                border: "none",
                borderRadius: "6px",
                fontWeight: "bold",
                cursor: "pointer",
                transition: "background-color 0.2s ease-in-out",
              }}
              onMouseOver={(e) => e.currentTarget.style.backgroundColor = "#5a6268"}
              onMouseOut={(e) => e.currentTarget.style.backgroundColor = "#6c757d"}
            >
              취소
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

const inputStyle = {
  padding: "10px 14px",
  border: "1px solid #ccc",
  borderRadius: "6px",
  fontSize: "15px",
  outline: "none",
  transition: "border-color 0.2s ease-in-out",
};