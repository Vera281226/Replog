import React, { useState } from 'react';
import ReviewContent from './ReviewContent';
import ReviewModal from './ReviewModal';
import ReportButton from '../common/ReportButton';

function ReviewItem({ review, allReviews, onCommentAdded, memberId }) {
  const [liked, setLiked] = useState(review.liked || false);
  const [likeCount, setLikeCount] = useState(review.likeCount || 0);
  const [showEditModal, setShowEditModal] = useState(false);

  // 대댓글 수정용 상태
  const [editingReplyId, setEditingReplyId] = useState(null);
  const [replyEdits, setReplyEdits] = useState({});

  const replies = allReviews?.filter(
    r => r.gnum === review.reviewId && r.reviewId !== r.gnum
  ) || [];

  const handleEditClick = () => setShowEditModal(true);
  const handleModalClose = () => setShowEditModal(false);
  const handleReviewUpdated = () => {
    setShowEditModal(false);
    onCommentAdded();
  };

  return (
    <div className="p-4 border rounded shadow-sm">
      <ReviewContent
        review={review}
        liked={liked}
        setLiked={setLiked}
        likeCount={likeCount}
        setLikeCount={setLikeCount}
        memberId={memberId}
        onCommentAdded={onCommentAdded}
        onEditClick={handleEditClick}
        replies={replies}
        editingReplyId={editingReplyId}
        setEditingReplyId={setEditingReplyId}
        replyEdits={replyEdits}
        setReplyEdits={setReplyEdits}
        // 신고 버튼을 prop으로 전달
        reportButton={
          <ReportButton
            targetType="REVIEW"
            targetId={review.reviewId}
            buttonText="신고"
            buttonStyle="small"
          />
        }
      />
      {showEditModal && (
        <ReviewModal
          isEdit={true}
          initialData={{
            reviewId: review.reviewId,
            cont: review.cont,
            rating: review.rating,
            isSpoiler: review.isSpoiler
          }}
          contentId={review.contentId}
          memberId={memberId}
          onClose={handleModalClose}
          onReviewCreated={handleReviewUpdated}
        />
      )}
    </div>
  );
}

export default ReviewItem;
