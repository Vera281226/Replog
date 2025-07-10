import React, { useEffect, useState } from "react";

const TiptapToolbar = ({ editor }) => {
  // ✅ html에 붙는 클래스 감지
  const [isDark, setIsDark] = useState(document.documentElement.classList.contains("dark-mode"));

  useEffect(() => {
    const observer = new MutationObserver(() => {
      setIsDark(document.documentElement.classList.contains("dark-mode"));
    });

    observer.observe(document.documentElement, { attributes: true, attributeFilter: ["class"] });

    return () => observer.disconnect();
  }, []);

  if (!editor) return null;

  const buttonStyle = (active) => ({
    padding: "6px 10px",
    fontSize: "16px",
    border: "none",
    backgroundColor: active
      ? isDark
        ? "#3a2f4e"
        : "#e0f0ff"
      : "transparent",
    color: active
      ? isDark
        ? "#bfa6ff"
        : "#007bff"
      : isDark
      ? "#ccc"
      : "#444",
    borderRadius: "4px",
    cursor: "pointer",
    transition: "background-color 0.2s ease-in-out",
  });

  const selectStyle = {
    padding: "6px 10px",
    fontSize: "14px",
    borderRadius: "4px",
    border: `1px solid ${isDark ? "#555" : "#ccc"}`,
    backgroundColor: isDark ? "#2b2b2b" : "#fff",
    color: isDark ? "#f5f5f5" : "#000",
  };

  const wrapperStyle = {
    display: "flex",
    gap: "8px",
    padding: "10px",
    marginBottom: "10px",
    border: `1px solid ${isDark ? "#444" : "#ddd"}`,
    borderRadius: "8px",
    backgroundColor: isDark ? "#2a2a2a" : "#f9f9f9",
    alignItems: "center",
    flexWrap: "wrap",
  };

  return (
    <div style={wrapperStyle}>
      <select
        onChange={(e) => {
          const value = e.target.value;
          if (value === "paragraph") editor.chain().focus().setParagraph().run();
          else editor.chain().focus().toggleHeading({ level: Number(value) }).run();
        }}
        defaultValue="paragraph"
        style={selectStyle}
      >
        <option value="paragraph">본문</option>
        <option value="1">큰제목</option>
        <option value="2">작은제목</option>
      </select>

      <button type="button" onClick={() => editor.chain().focus().toggleBold().run()} style={buttonStyle(editor.isActive("bold"))}>
        <b>B</b>
      </button>
      <button type="button" onClick={() => editor.chain().focus().toggleItalic().run()} style={buttonStyle(editor.isActive("italic"))}>
        <i>I</i>
      </button>
      <button type="button" onClick={() => editor.chain().focus().toggleUnderline().run()} style={buttonStyle(editor.isActive("underline"))}>
        <u>U</u>
      </button>

      <button type="button" onClick={() => editor.chain().focus().setTextAlign("left").run()} style={buttonStyle(editor.isActive({ textAlign: "left" }))}>
        ⬅
      </button>
      <button type="button" onClick={() => editor.chain().focus().setTextAlign("center").run()} style={buttonStyle(editor.isActive({ textAlign: "center" }))}>
        ⬍
      </button>
      <button type="button" onClick={() => editor.chain().focus().setTextAlign("right").run()} style={buttonStyle(editor.isActive({ textAlign: "right" }))}>
        ➡
      </button>

      <button
        type="button"
        title="내용 초기화"
        onClick={() => {
          editor.commands.clearNodes();
          editor.commands.clearContent();
        }}
        style={buttonStyle(false)}
      >
        ❌
      </button>
    </div>
  );
};

export default TiptapToolbar;