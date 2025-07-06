import api from '../error/api/interceptor';

export const useReviewApi = () => {
  const toggleLike = async (reviewId, memberId) => {
    const res = await api.post(`/reviews/${reviewId}/like`, { memberId });
    return res.data;
  };

  const updateReview = async (reviewId, data) => {
    await api.patch(`/reviews/${reviewId}`, data);
  };

  const deleteReview = async (reviewId) => {
    await api.delete(`/reviews/${reviewId}`);
  };

  const postReply = async (reviewId, data) => {
    await api.post(`/reviews/${reviewId}/comments`, data);
  };

  const updateReply = async (replyId, data) => {
    await api.patch(`/reviews/${replyId}/comments`, data);
  };

  const deleteReply = async (replyId) => {
    await api.delete(`/reviews/${replyId}/comments`);
  };

  return {
    toggleLike,
    updateReview,
    deleteReview,
    postReply,
    updateReply,
    deleteReply
  };
};
