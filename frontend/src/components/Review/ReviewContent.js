import React, { useState } from 'react';
import { useReviewApi } from '../../hooks/useReviewApi';
import StarRating from './StarRating';
import InfoModal from '../InfoModal';
import ReplyForm from './ReplyForm';
import ReplyList from './ReplyList';
import './ReviewContent.css';

function ReviewContent({
  review,
  memberId,
  onCommentAdded,
  replies,
  editingReplyId,
  setEditingReplyId,
  replyEdits,
  setReplyEdits,
  onEditClick
}) {
  const [liked, setLiked] = useState(review.isLiked);
  const [likeCount, setLikeCount] = useState(review.likeCount);
  const [showSpoiler, setShowSpoiler] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [showReplyInput, setShowReplyInput] = useState(false);
  const [replyText, setReplyText] = useState('');
  const { toggleLike, deleteReview } = useReviewApi();

  const isEdited = new Date(review.createdAt).getTime() !== new Date(review.updatedAt).getTime();

  const handleLike = async () => {
    try {
      const res = await toggleLike(review.reviewId, memberId);
      setLiked(res.liked);
      setLikeCount(res.likeCount);
    } catch {
      alert('좋아요 실패');
    }
  };

  const handleDelete = async () => {
    try {
      await deleteReview(review.reviewId);
      onCommentAdded();
    } catch {
      alert('리뷰 삭제 실패');
    }
  };

  return (
    <div className="review-box">
      <div className="review-header">
        <div className="header-left">
          <div className="review-meta" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div className="review-writer">{review.memberId}</div>
            <StarRating rating={review.rating || 0} />
          </div>
          <div className="review-date">
            {new Date(review.updatedAt).toLocaleString('ko-KR', {
              year: 'numeric',
              month: '2-digit',
              day: '2-digit',
              hour: '2-digit',
              minute: '2-digit',
            })}
            {isEdited && <span className="review-edited">(수정됨)</span>}
          </div>
        </div>
        {review.memberId !== memberId && (
          <button
            onClick={() => alert('신고 기능은 추후 구현')}
            className="btn-report-top"
          >
            🚨 신고하기
          </button>
        )}
      </div>

      <div className="review-content">
        {review.isSpoiler && !showSpoiler ? (
          <>
            <span className="review-spoiler">⚠️ 스포일러 포함</span>
            <button onClick={() => setShowSpoiler(true)} className="btn-show">
              보기
            </button>
          </>
        ) : (
          review.cont
        )}
      </div>

      <div className="review-buttons">
        <button className="like-btn" onClick={handleLike}>
          <span className={liked ? 'heart-icon liked' : 'heart-icon'}>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill={liked ? "#ab97ec" : "none"}
              stroke="#ab97ec"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <path d="M20.8 4.6c-1.6-1.7-4.3-1.7-6 0L12 7.4l-2.8-2.8c-1.7-1.7-4.4-1.7-6 0-1.7 1.6-1.7 4.3 0 6l8.8 8.8 8.8-8.8c1.7-1.6 1.7-4.3 0-6z" />
            </svg>
          </span>
          {' '}
          {likeCount}
        </button>

        <button className="reply-btn" onClick={() => setShowReplyInput(prev => !prev)}>
          댓글
        </button>

        {review.memberId === memberId && (
          <>
            <button className="edit-btn" onClick={onEditClick}>수정</button>
            <button className="delete-btn" onClick={() => setShowDeleteConfirm(true)}>삭제</button>
          </>
        )}
      </div>

      {showReplyInput && (
        <ReplyForm
          show={true}
          setShow={setShowReplyInput}
          replyText={replyText}
          setReplyText={setReplyText}
          parentId={review.reviewId}
          onCommentAdded={onCommentAdded}
          memberId={memberId}
        />
      )}

      {replies && replies.length > 0 && (
        <div className="review-replies">
          <ReplyList
            replies={replies}
            editingReplyId={editingReplyId}
            setEditingReplyId={setEditingReplyId}
            replyEdits={replyEdits}
            setReplyEdits={setReplyEdits}
            onCommentAdded={onCommentAdded}
            memberId={memberId}
          />
        </div>
      )}

      <InfoModal
        isOpen={showDeleteConfirm}
        type="warning"
        title="리뷰 삭제"
        message="정말 삭제하시겠습니까?"
        confirmLabel="삭제"
        cancelLabel="취소"
        onConfirm={() => {
          handleDelete();
          setShowDeleteConfirm(false);
        }}
        onCancel={() => setShowDeleteConfirm(false)}
      />
    </div>
  );
}

export default ReviewContent;
