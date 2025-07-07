import React, { useState } from 'react';
import ReplyItem from './ReplyItem';

function ReplyList({
  replies,
  editingReplyId,
  setEditingReplyId,
  replyEdits,
  setReplyEdits,
  onCommentAdded,
  memberId
}) {
  const [showAll, setShowAll] = useState(false);

  const sortedReplies = [...replies].sort((a, b) => {
    if (a.memberId === memberId && b.memberId !== memberId) return -1;
    if (a.memberId !== memberId && b.memberId === memberId) return 1;
    return new Date(b.createdAt) - new Date(a.createdAt);
  });

  const visible = showAll ? sortedReplies : sortedReplies.slice(0, 1);

  if (replies.length === 0) return null;

  return (
    <div className="mt-4 bg-gray-50 p-2 rounded space-y-2">
      {visible.map(reply => (
        <ReplyItem
          key={reply.reviewId}
          reply={reply}
          memberId={memberId}
          editingReplyId={editingReplyId}
          setEditingReplyId={setEditingReplyId}
          replyEdits={replyEdits}
          setReplyEdits={setReplyEdits}
          onCommentAdded={onCommentAdded}
        />
      ))}

      {sortedReplies.length > 1 && !showAll && (
  <div style={{ textAlign: 'center', marginTop: '8px' }}>
    <button onClick={() => setShowAll(true)} className="reply-btn">
      더 보기
    </button>
  </div>
)}


    </div>
  );
}

export default ReplyList;
