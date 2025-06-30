import React from 'react';
import { useReviewApi } from '../../hooks/useReviewApi';

function ReplyForm({ show, setShow, replyText, setReplyText, parentId, onCommentAdded, memberId }) {
  const { postReply } = useReviewApi();

  const handleSubmit = async () => {
    if (!replyText.trim()) {
      alert('댓글 내용을 입력하세요.');
      return;
    }

    try {
      await postReply(parentId, {
        memberId,
        cont: replyText,
        rating: 0
      });
      setReplyText('');
      setShow(false);
      onCommentAdded();
    } catch (err) {
      alert('댓글 등록 실패');
    }
  };

  if (!show) return null;

  return (
    <div className="mt-2">
      <textarea
        className="w-full border p-2 rounded"
        value={replyText}
        onChange={(e) => setReplyText(e.target.value)}
        rows={2}
        placeholder="댓글을 입력하세요"
      />
      <div className="mt-1 flex justify-end gap-2">
        <button onClick={handleSubmit} className="bg-blue-500 text-white px-3 py-1 rounded">등록</button>
        <button onClick={() => setShow(false)} className="border px-3 py-1 rounded">취소</button>
      </div>
    </div>
  );
}

export default ReplyForm;
