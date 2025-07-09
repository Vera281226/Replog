import { useState } from 'react';
import ContentDetail from './ContentDetail';
import ReviewList from '../Review/ReviewList';

export default function ContentPage({ contentId, ...props }) {
  const [userReviewRating, setUserReviewRating] = useState(null);

  return (
    <div>
      <ContentDetail
        {...props}
        contentId={contentId}
        userReviewRating={userReviewRating}
      />
      <ReviewList
        contentId={contentId}
        memberId={props.memberId}
        onCommentAdded={() => {}}
        openModal={() => {}}
        setUserReviewRating={setUserReviewRating}
      />
    </div>
  );
}
