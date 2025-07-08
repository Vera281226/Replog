import React, { useState } from 'react';
import ReviewContent from './ReviewContent';
import ReplyForm from './ReplyForm';
import ReplyList from './ReplyList';

function ReviewItem({ review, allReviews, onCommentAdded, memberId }) {
  const [liked, setLiked] = useState(review.liked || false);
  const [likeCount, setLikeCount] = useState(review.likeCount || 0);
  const [showReplyInput, setShowReplyInput] = useState(false);
  const [replyText, setReplyText] = useState('');
  const [editMode, setEditMode] = useState(false);
  const [editedContent, setEditedContent] = useState(review.cont);
  const [editedRating, setEditedRating] = useState(review.rating || 0);
  const [editingReplyId, setEditingReplyId] = useState(null);
  const [replyEdits, setReplyEdits] = useState({});

  const replies = allReviews?.filter(
    r => r.gnum === review.reviewId && r.reviewId !== r.gnum
  ) || [];

  return (
    <li className="p-4 border rounded shadow-sm">
      <ReviewContent
        review={review}
        liked={liked}
        setLiked={setLiked}
        likeCount={likeCount}
        setLikeCount={setLikeCount}
        memberId={memberId}
        onCommentAdded={onCommentAdded}
        setShowReplyInput={setShowReplyInput}
      />


      <ReplyForm
        show={showReplyInput}
        setShow={setShowReplyInput}
        replyText={replyText}
        setReplyText={setReplyText}
        parentId={review.reviewId}
        onCommentAdded={onCommentAdded}
        memberId={memberId}
      />

      <ReplyList
        replies={replies}
        editingReplyId={editingReplyId}
        setEditingReplyId={setEditingReplyId}
        replyEdits={replyEdits}
        setReplyEdits={setReplyEdits}
        onCommentAdded={onCommentAdded}
        memberId={memberId}
      />
    </li>
  );
}

export default ReviewItem;
