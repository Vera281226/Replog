import React from "react";

const TiptapToolbar = ({ editor }) => {
  if (!editor) return null;

  const buttonStyle = (active) => ({
    padding: "6px 8px",
    fontSize: "18px",
    border: "none",
    background: "transparent",
    cursor: "pointer",
    color: active ? "#1e90ff" : "#444",
  });

  return (
    <div style={{
      display: "flex",
      gap: "6px",
      padding: "8px",
      borderBottom: "1px solid #ccc",
      alignItems: "center",
    }}>
      {/* 스타일 선택 */}
      <select
        onChange={(e) => {
          const value = e.target.value;
          if (value === "paragraph") editor.chain().focus().setParagraph().run();
          else editor.chain().focus().toggleHeading({ level: Number(value) }).run();
        }}
        defaultValue="paragraph"
        style={{ padding: "4px 8px", fontSize: "14px" }}
      >
        <option value="paragraph">Normal</option>
        <option value="1">Heading 1</option>
        <option value="2">Heading 2</option>
      </select>

      {/* 스타일 버튼들 */}
      <button type="button" onClick={() => editor.chain().focus().toggleBold().run()}
              style={buttonStyle(editor.isActive("bold"))}>🅱</button>

      <button type="button" onClick={() => editor.chain().focus().toggleItalic().run()}
              style={buttonStyle(editor.isActive("italic"))}>𝘐</button>

      <button type="button" onClick={() => editor.chain().focus().toggleUnderline().run()}
              style={buttonStyle(editor.isActive("underline"))}>𝕌</button>

      {/* 정렬 */}
      <button type="button" onClick={() => editor.chain().focus().setTextAlign("left").run()}
              style={buttonStyle(editor.isActive({ textAlign: "left" }))}>⬅️</button>

      <button type="button" onClick={() => editor.chain().focus().setTextAlign("center").run()}
              style={buttonStyle(editor.isActive({ textAlign: "center" }))}>⬇️</button>

      <button type="button" onClick={() => editor.chain().focus().setTextAlign("right").run()}
              style={buttonStyle(editor.isActive({ textAlign: "right" }))}>➡️</button>

      {/* 초기화 버튼 (지우개 아이콘) */}
      <button
        type="button"
        onClick={() => {
          editor.commands.clearNodes();
          editor.commands.clearContent();
        }}
        title="내용 초기화"
        style={buttonStyle(false)}
      >
        🧽
      </button>
    </div>
  );
};

export default TiptapToolbar;
