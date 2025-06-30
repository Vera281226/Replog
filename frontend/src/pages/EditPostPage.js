import { useState } from "react";
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
        content: editor.getHTML(),
      });
      navigate(`/boards/${postNo}`);
    } catch (err) {
      alert("글 수정에 실패했습니다.");
    }
  };

  if (!postNo) return <div>잘못된 접근입니다.</div>;

  return (
    <div style={{ padding: "20px", maxWidth: "800px", margin: "0 auto" }}>
      <h2 style={{ marginBottom: "20px" }}>글 수정</h2>
      <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
        <input
          type="text"
          name="memberId"
          placeholder="아이디"
          value={form.memberId}
          onChange={handleChange}
          required
          disabled
        />
        <input
          type="text"
          name="nickname"
          placeholder="닉네임"
          value={form.nickname}
          onChange={handleChange}
          required
          disabled
        />
        <select
          name="category"
          value={form.category}
          onChange={handleChange}
          required
        >
          <option value="" disabled hidden>선택하세요</option>
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
        />

        <TiptapToolbar editor={editor} />
        <div style={{ border: "1px solid #ccc", borderRadius: "4px", padding: "10px", minHeight: "200px" }}>
          <EditorContent editor={editor} />
        </div>

        <button
          type="submit"
          style={{
            padding: "10px",
            backgroundColor: "#28a745",
            color: "white",
            border: "none",
            borderRadius: "4px"
          }}
        >
          수정 완료
        </button>
      </form>
    </div>
  );
}
