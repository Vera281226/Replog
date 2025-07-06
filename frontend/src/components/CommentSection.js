import { useState } from "react";
import axios from "../error/api/interceptor";
import { ErrorModal } from "../error/components/ErrorModal";
import BannedWordFilterModal, { checkBannedWords } from "../components/BannedWordFilterModal";


export default function CommentSection({
  postNo,
  userId,
  nickname,
  comments,
  setComments,
  showMessage,
  isAuthenticated,
}) {
  const [newComment, setNewComment] = useState("");
  const [editingCommentNo, setEditingCommentNo] = useState(null);
  const [editingContent, setEditingContent] = useState("");
  const [commentToDelete, setCommentToDelete] = useState(null);
  const [bannedModalOpen, setBannedModalOpen] = useState(false);
  const [bannedWordsMatched, setBannedWordsMatched] = useState([]);

  const handleCommentSubmit = async () => {
    if (!isAuthenticated) {
      showMessage("로그인 후 댓글 작성이 가능합니다.", "error");
      return;
    }
    if (!newComment.trim()) return;

    const { hasBanned, matchedWords } = checkBannedWords(newComment);
    if (hasBanned) {
      setBannedWordsMatched(matchedWords);
      setBannedModalOpen(true);
      return;
    }

    try {
      const res = await axios.post("/comments", {
        memberId: userId,
        nickname,
        postNo: parseInt(postNo),
        content: newComment,
      });
      setComments((prev) => [...prev, { ...res.data, likes: 0, isLiked: false }]);
      setNewComment("");
    } catch {
      showMessage("댓글 작성에 실패했습니다.", "error");
    }
  };

  const handleDeleteComment = async () => {
    try {
      await axios.delete(`/comments/${commentToDelete}`);
      setComments((prev) => prev.filter((c) => c.commentNo !== commentToDelete));
    } catch {
      showMessage("댓글 삭제에 실패했습니다.", "error");
    } finally {
      setCommentToDelete(null);
    }
  };

  const handleUpdateComment = async (commentNo) => {
    const { hasBanned, matchedWords } = checkBannedWords(editingContent);
    if (hasBanned) {
      setBannedWordsMatched(matchedWords);
      setBannedModalOpen(true);
      return;
    }

    try {
      const res = await axios.put(`/comments/${commentNo}`, {
        memberId: userId,
        nickname,
        postNo: parseInt(postNo),
        content: editingContent,
      });
      setComments((prev) =>
        prev.map((c) =>
          c.commentNo === commentNo ? { ...c, content: res.data.content, updatedAt: res.data.updatedAt } : c
        )
      );
      setEditingCommentNo(null);
      setEditingContent("");
      showMessage("댓글이 수정되었습니다.", "success");
    } catch {
      showMessage("댓글 수정에 실패했습니다.", "error");
    }
  };

  const toggleCommentLike = async (commentNo) => {
    try {
      const res = await axios.post(`/comments/${commentNo}/like`, null, {
        params: { memberId: userId },
      });
      const isLiked = res.data === "liked";
      setComments((prev) =>
        prev.map((c) =>
          c.commentNo === commentNo
            ? { ...c, isLiked, likes: isLiked ? c.likes + 1 : c.likes - 1 }
            : c
        )
      );
    } catch {
      showMessage("댓글 좋아요 처리 실패", "error");
    }
  };

  return (
    <div className="comment-section">
      <h3>댓글</h3>

      {comments.map((comment) => {
        const isEdited = comment.updatedAt !== null;
        const timeLabel = isEdited ? comment.updatedAt : comment.createdAt;

        return (
          <div className="comment-box" key={comment.commentNo}>
            <div className="comment-meta">
              <div className="comment-info">
                <span>{comment.nickname}</span>
                <span className="comment-date">
                  {new Date(timeLabel).toLocaleString()} {isEdited && "(수정됨)"}
                </span>
              </div>
              <button
                className={`comment-like ${comment.isLiked ? "liked" : ""}`}
                onClick={() => toggleCommentLike(comment.commentNo)}
              >
                추천 {comment.likes}
              </button>
            </div>

            {editingCommentNo === comment.commentNo ? (
              <>
                <textarea
                  value={editingContent}
                  onChange={(e) => setEditingContent(e.target.value)}
                  className="comment-edit-area"
                />
                <div className="comment-edit-buttons">
                  <button onClick={() => handleUpdateComment(comment.commentNo)} className="btn-confirm">
                    완료
                  </button>
                  <button onClick={() => setEditingCommentNo(null)} className="btn-cancel">
                    취소
                  </button>
                </div>
              </>
            ) : (
              <div className="comment-content-row">
                <div className="comment-content">{comment.content}</div>
                {comment.memberId === userId && (
                  <div className="comment-action-buttons">
                    <button
                      onClick={() => {
                        setEditingCommentNo(comment.commentNo);
                        setEditingContent(comment.content);
                      }}
                    >
                      수정
                    </button>
                    <button onClick={() => setCommentToDelete(comment.commentNo)} className="btn-delete">
                      삭제
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>
        );
      })}

      <div className="comment-input-section">
        <textarea
          placeholder={isAuthenticated ? "댓글을 입력하세요" : "로그인 후 댓글을 작성할 수 있습니다"}
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          disabled={!isAuthenticated}
        />
        <button onClick={handleCommentSubmit} disabled={!isAuthenticated}>
          댓글 작성
        </button>
      </div>

      <ErrorModal
        isOpen={commentToDelete !== null}
        title="댓글 삭제 확인"
        message="댓글을 삭제하시겠습니까?"
        onConfirm={handleDeleteComment}
        onCancel={() => setCommentToDelete(null)}
      />

      <BannedWordFilterModal
        isOpen={bannedModalOpen}
        matchedWords={bannedWordsMatched}
        onClose={() => setBannedModalOpen(false)}
      />
    </div>
  );
}
