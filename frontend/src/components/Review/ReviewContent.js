import React, { useState } from 'react';
import { useReviewApi } from '../../hooks/useReviewApi';
import StarRating from './StarRating';

function ReviewContent({
  review,
  memberId,
  liked,
  setLiked,
  likeCount,
  setLikeCount,
  editMode,
  setEditMode,
  editedContent,
  setEditedContent,
  editedRating,
  setEditedRating,
  onCommentAdded,
  setShowReplyInput
}) {
  const [showSpoiler, setShowSpoiler] = useState(false);
  const { toggleLike, updateReview, deleteReview } = useReviewApi();

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

  const handleUpdate = async () => {
    try {
      await updateReview(review.reviewId, {
        memberId,
        cont: editedContent,
        rating: editedRating
      });
      setEditMode(false);
      onCommentAdded();
    } catch {
      alert('리뷰 수정 실패');
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;
    try {
      await deleteReview(review.reviewId);
      onCommentAdded();
    } catch {
      alert('리뷰 삭제 실패');
    }
  };

  const handleEditClick = () => {
    const query = new URLSearchParams({
      reviewId: review.reviewId,
      memberId,
      cont: review.cont,
      rating: review.rating,
      isSpoiler: review.isSpoiler,
    }).toString();

    window.open(
      `/review-edit-popup?${query}`,
      'ReviewEditPopup',
      'width=500,height=600,top=100,left=100'
    );
  };

  return (
    <div>
      <div className="flex justify-between">
        <div className="font-semibold">{review.memberId}</div>
        <div className="text-sm text-gray-500">
          {new Date(review.updatedAt).toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
          })}
          {isEdited && (
            <span className="ml-1 text-xs text-gray-400">(수정됨)</span>
          )}
        </div>
      </div>

      {editMode ? (
        <div className="space-y-2 mt-2">
          <textarea
            className="w-full border p-2 rounded"
            value={editedContent}
            onChange={(e) => setEditedContent(e.target.value)}
            rows={3}
          />
          <input
            type="number"
            min={1}
            max={5}
            value={editedRating}
            onChange={(e) => setEditedRating(parseInt(e.target.value))}
            className="border px-2 py-1 rounded"
          />
          <div className="flex gap-2">
            <button onClick={handleUpdate} className="bg-green-500 text-white px-3 py-1 rounded">저장</button>
            <button onClick={() => setEditMode(false)} className="border px-3 py-1 rounded">취소</button>
          </div>
        </div>
      ) : (
        <>
          <StarRating rating={review.rating || 0} />
          <div className="mt-1 text-sm">
            {review.isSpoiler && !showSpoiler ? (
              <>
                <span className="italic text-gray-500">⚠️ 스포일러 포함</span>
                <button onClick={() => setShowSpoiler(true)} className="ml-2 underline text-blue-500 text-xs">보기</button>
              </>
            ) : (
              review.cont
            )}
          </div>
        </>
      )}

      <div className="mt-3 flex gap-4 text-sm">
        <button onClick={handleLike} className="flex items-center gap-1">
          <span>{liked ? '💖' : '🤍'}</span>
          <span>{likeCount}</span>
        </button>
        <button onClick={() => setShowReplyInput(prev => !prev)} className="text-blue-500 hover:underline">
          답글
        </button>
      </div>

      {review.memberId === memberId && !editMode && (
        <div className="mt-2 flex gap-3 text-sm">
          <button onClick={handleEditClick} className="text-blue-500">수정</button>
          <button onClick={handleDelete} className="text-red-500">삭제</button>
        </div>
      )}

      {review.memberId !== memberId && (
        <div className="mt-2 text-sm">
          <button
            onClick={() => alert('신고 기능은 추후 구현')}
            className="text-red-500 hover:underline"
          >
            🚨 신고
          </button>
        </div>
      )}
    </div>
  );
}

export default ReviewContent;
