// src/components/Review/ReplyItem.js
import React, { useState } from 'react';
import { useReviewApi } from '../../hooks/useReviewApi';
import InfoModal from '../InfoModal';

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
  const [showDeleteModal, setShowDeleteModal] = useState(false);

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
    }
  };

  const handleDeleteClick = () => {
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      await deleteReply(reply.reviewId);
      setShowDeleteModal(false);
      onCommentAdded();
    } catch {
      setShowDeleteModal(false);
    }
  };

  const handleDeleteCancel = () => {
    setShowDeleteModal(false);
  };

  return (
    <>
      <div
        style={{
          marginLeft: '16px',
          paddingLeft: '12px',
          borderLeft: '2px solid #eee',
          marginTop: '12px'
        }}
      >
        <div className="review-header">
          <div className="header-left">
            <div className="review-writer">{reply.memberId}</div>
            <div className="review-date">
              {reply.createdAt &&
                new Date(reply.createdAt).toLocaleString('ko-KR', {
                  year: 'numeric',
                  month: '2-digit',
                  day: '2-digit',
                  hour: '2-digit',
                  minute: '2-digit'
                })}
            </div>
          </div>
        </div>

        <div className="review-content">
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
                style={{
                  width: '100%',
                  fontSize: '15px',
                  padding: '8px',
                  borderRadius: '6px',
                  border: '1px solid #ccc',
                  backgroundColor: '#f8f8f8'
                }}
              />

              <div className="review-buttons">
                <button className="edit-btn" onClick={handleSave}>등록</button>
                <button className="delete-btn" onClick={() => setEditingReplyId(null)}>취소</button>
              </div>
            </>
          ) : (
            <>
              <p style={{ marginTop: '4px' }}>{reply.cont}</p>
              {reply.memberId === memberId && (
                <div className="review-buttons">
                  <button className="edit-btn" onClick={() => setEditingReplyId(reply.reviewId)}>수정</button>
                  <button className="delete-btn" onClick={handleDeleteClick}>삭제</button>
                </div>
              )}
            </>
          )}
        </div>
      </div>

      <InfoModal
        isOpen={showDeleteModal}
        type="warning"
        title="댓글 삭제"
        message="정말로 댓글을 삭제하시겠습니까?"
        confirmLabel="삭제"
        cancelLabel="취소"
        onConfirm={handleDeleteConfirm}
        onCancel={handleDeleteCancel}
      />
    </>
  );
}

export default ReplyItem;
