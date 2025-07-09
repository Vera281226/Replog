import React from 'react';
import { useReviewApi } from '../../hooks/useReviewApi';

function ReplyItem({
  reply,
  memberId,
  editingReplyId,
  setEditingReplyId,
  replyEdits,
  setReplyEdits,
  onCommentAdded
}) {
  const { updateReply, deleteReply } = useReviewApi();
  const isEditing = editingReplyId === reply.reviewId;

  const handleSave = async () => {
    try {
      await updateReply(reply.reviewId, {
        memberId,
        cont: replyEdits[reply.reviewId] || reply.cont,
        rating: 0
      });
      setEditingReplyId(null);
      onCommentAdded();
    } catch {
      alert('댓글 수정 실패');
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('댓글을 삭제하시겠습니까?')) return;
    try {
      await deleteReply(reply.reviewId);
      onCommentAdded();
    } catch {
      alert('댓글 삭제 실패');
    }
  };

  return (
    <div className="mt-2 ml-4 p-2 border-l border-gray-300">
      <div className="flex justify-between items-center">
        <span className="font-semibold text-sm">{reply.memberId}</span>{' '}
        <span className="text-xs text-gray-500">
          {reply.createdAt
            ? new Date(reply.createdAt).toLocaleString('ko-KR', {
              year: 'numeric',
              month: '2-digit',
              day: '2-digit',
              hour: '2-digit',
              minute: '2-digit'
            })
            : ''}
        </span>
      </div>

      <div className="text-sm mt-1 text-gray-800">
        {isEditing ? (
          <>
            <textarea
              className="reply-textarea"
              value={replyEdits[reply.reviewId] || reply.cont}
              onChange={(e) =>
                setReplyEdits({ ...replyEdits, [reply.reviewId]: e.target.value })
              }
              onInput={(e) => {
                e.target.style.height = 'auto';
                e.target.style.height = e.target.scrollHeight + 'px';
              }}
              rows={1}
              placeholder="댓글을 입력하세요"
            />

            <div className="reply-actions">
              <button onClick={handleSave} className="reply-btn">등록</button>
              <button onClick={() => setEditingReplyId(null)} className="reply-btn cancel">취소</button>
            </div>

          </>
        ) : (
          <>
            <p className="mt-1">{reply.cont}</p>
            {reply.memberId === memberId && (
              <div className="reply-buttons text-sm mt-1 space-x-3" style={{ marginBottom: '10px' }}>
                <button className="reply-btn" onClick={() => setEditingReplyId(reply.reviewId)}>수정</button>
                <button className="reply-btn" onClick={handleDelete}>삭제</button>
              </div>


            )}
          </>
        )}
      </div>
    </div>
  );
}

export default ReplyItem;
