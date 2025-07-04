import { useState, useEffect } from "react"; 
import { useNavigate, useParams } from "react-router-dom";
import { useSelector } from "react-redux";
import axios from "axios";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Underline from "@tiptap/extension-underline";
import TextAlign from "@tiptap/extension-text-align";
import TiptapToolbar from "../components/TiptapToolbar";
import BannedWordFilterModal, { checkBannedWords } from "../components/BannedWordFilterModal";
import {
  selectCurrentUser,
  selectIsAuthenticated,
} from "../error/redux/authSlice";
import "./css/EditPostPage.css";

export default function EditPostPage() {
  const { postNo } = useParams();
  const navigate = useNavigate();
  const currentUser = useSelector(selectCurrentUser);
  const isAuthenticated = useSelector(selectIsAuthenticated);

  const [form, setForm] = useState(null);
  const [loading, setLoading] = useState(true);
  const [bannedModalOpen, setBannedModalOpen] = useState(false);
  const [bannedWordsMatched, setBannedWordsMatched] = useState([]);

  const editor = useEditor({
    extensions: [
      StarterKit,
      Underline,
      TextAlign.configure({ types: ["heading", "paragraph"] }),
    ],
    content: "",
  });

  useEffect(() => {
    if (!postNo) {
      alert("잘못된 접근입니다.");
      navigate("/boards");
      return;
    }

    const fetchPost = async () => {
      try {
        const res = await axios.get(`/api/posts/${postNo}`);
        const post = res.data;

        if (!isAuthenticated || currentUser?.memberId !== post.memberId) {
          alert("수정 권한이 없습니다.");
          navigate(`/boards/${postNo}`);
          return;
        }

        setForm({
          nickname: post.nickname,
          category: post.category,
          title: post.title,
        });

        editor?.commands.setContent(post.content);
      } catch (err) {
        console.error(err);
        alert("게시글을 불러오지 못했습니다.");
        navigate("/boards");
      } finally {
        setLoading(false);
      }
    };

    fetchPost();
  }, [postNo, currentUser, isAuthenticated, navigate, editor]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.category || !form.title.trim()) {
      alert("카테고리와 제목을 모두 입력해주세요.");
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
      await axios.put(`/api/posts/${postNo}`, {
        ...form,
        content: contentHtml,
        memberId: currentUser?.memberId,
      });
      navigate(`/boards/${postNo}`);
    } catch (err) {
      console.error(err);
      alert("글 수정에 실패했습니다.");
    }
  };

  if (loading || !form) return <div>로딩 중...</div>;

  return (
    <div className="edit-post-wrapper">
      <div className="edit-post-container">
        <h2 className="edit-post-title">글 수정</h2>

        <form className="edit-post-form" onSubmit={handleSubmit}>
          <input
            type="text"
            name="nickname"
            value={form.nickname}
            disabled
            className="edit-post-input"
          />

          <select
            name="category"
            value={form.category}
            onChange={handleChange}
            required
            className="edit-post-select"
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
            value={form.title}
            onChange={handleChange}
            required
            className="edit-post-input"
          />

          <TiptapToolbar editor={editor} />

          <div className="edit-post-editor">
            <EditorContent editor={editor} />
          </div>

          <div className="edit-post-buttons">
            <button type="submit" className="edit-post-submit">
              수정 완료
            </button>
            <button
              type="button"
              onClick={() => navigate(`/boards/${postNo}`)}
              className="edit-post-cancel"
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
    </div>
  );
}