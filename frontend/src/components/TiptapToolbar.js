import React from "react";

const TiptapToolbar = ({ editor }) => {
  if (!editor) return null;

  const buttonStyle = (active) => ({
    padding: "6px 10px",
    fontSize: "16px",
    border: "none",
    backgroundColor: active ? "#e0f0ff" : "transparent",
    color: active ? "#007bff" : "#444",
    borderRadius: "4px",
    cursor: "pointer",
    transition: "background-color 0.2s ease-in-out",
  });

  return (
    <div
      style={{
        display: "flex",
        gap: "8px",
        padding: "10px",
        marginBottom: "10px",
        border: "1px solid #ddd",
        borderRadius: "8px",
        backgroundColor: "#f9f9f9",
        alignItems: "center",
        flexWrap: "wrap",
      }}
    >
      {/* 스타일 선택 */}
      <select
        onChange={(e) => {
          const value = e.target.value;
          if (value === "paragraph") editor.chain().focus().setParagraph().run();
          else editor.chain().focus().toggleHeading({ level: Number(value) }).run();
        }}
        defaultValue="paragraph"
        style={{
          padding: "6px 10px",
          fontSize: "14px",
          borderRadius: "4px",
          border: "1px solid #ccc",
          backgroundColor: "#fff",
        }}
      >
        <option value="paragraph">본문</option>
        <option value="1">큰제목</option>
        <option value="2">작은제목</option>
      </select>

      {/* 스타일 버튼들 */}
      <button type="button" onClick={() => editor.chain().focus().toggleBold().run()} style={buttonStyle(editor.isActive("bold"))}>
        <b>B</b>
      </button>
      <button type="button" onClick={() => editor.chain().focus().toggleItalic().run()} style={buttonStyle(editor.isActive("italic"))}>
        <i>I</i>
      </button>
      <button type="button" onClick={() => editor.chain().focus().toggleUnderline().run()} style={buttonStyle(editor.isActive("underline"))}>
        <u>U</u>
      </button>

      {/* 정렬 버튼 */}
      <button type="button" onClick={() => editor.chain().focus().setTextAlign("left").run()} style={buttonStyle(editor.isActive({ textAlign: "left" }))}>
        ⬅
      </button>
      <button type="button" onClick={() => editor.chain().focus().setTextAlign("center").run()} style={buttonStyle(editor.isActive({ textAlign: "center" }))}>
        ⬍
      </button>
      <button type="button" onClick={() => editor.chain().focus().setTextAlign("right").run()} style={buttonStyle(editor.isActive({ textAlign: "right" }))}>
        ➡
      </button>

      {/* 초기화 버튼 */}
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