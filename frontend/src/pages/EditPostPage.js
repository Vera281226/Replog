import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Underline from "@tiptap/extension-underline";
import TextAlign from "@tiptap/extension-text-align";
import TiptapToolbar from "../components/TiptapToolbar";

export default function EditPostPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const post = location.state?.post;
  const postNo = post?.postNo;

  const [form, setForm] = useState({
    memberId: post?.memberId || "",
    nickname: post?.nickname || "",
    category: post?.category || "",
    title: post?.title || "",
  });

  const editor = useEditor({
    extensions: [
      StarterKit,
      Underline,
      TextAlign.configure({ types: ["heading", "paragraph"] }),
    ],
    content: post?.content || "",
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
      await axios.put(`/api/posts/${postNo}`, {
        ...form,
        content: editor?.getHTML() || "",
      });
      navigate(`/boards/${postNo}`);
    } catch (err) {
      alert("글 수정에 실패했습니다.");
    }
  };

  useEffect(() => {
    if (!postNo) {
      alert("잘못된 접근입니다.");
      navigate("/boards");
    }
  }, [postNo, navigate]);

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
        <h2 style={{ marginBottom: "24px", fontSize: "24px", fontWeight: "bold", color: "#333" }}>글 수정</h2>
        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
          <input
            type="text"
            name="memberId"
            placeholder="아이디"
            value={form.memberId}
            onChange={handleChange}
            required
            disabled
            style={inputStyle}
          />
          <input
            type="text"
            name="nickname"
            placeholder="닉네임"
            value={form.nickname}
            onChange={handleChange}
            required
            disabled
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
              style={submitButtonStyle}
              onMouseOver={(e) => e.currentTarget.style.backgroundColor = "#218838"}
              onMouseOut={(e) => e.currentTarget.style.backgroundColor = "#28a745"}
            >
              수정 완료
            </button>

            <button
              type="button"
              onClick={() => navigate(`/boards/${postNo}`)}
              style={cancelButtonStyle}
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
  backgroundColor: "#fff",
  transition: "border-color 0.2s ease-in-out",
};

const submitButtonStyle = {
  flex: 1,
  padding: "12px",
  backgroundColor: "#28a745",
  color: "white",
  border: "none",
  borderRadius: "6px",
  fontWeight: "bold",
  cursor: "pointer",
  transition: "background-color 0.2s ease-in-out",
};

const cancelButtonStyle = {
  flex: 1,
  padding: "12px",
  backgroundColor: "#6c757d",
  color: "white",
  border: "none",
  borderRadius: "6px",
  fontWeight: "bold",
  cursor: "pointer",
  transition: "background-color 0.2s ease-in-out",
};
