package pack.service.board;

public interface CommentLikeService {
    boolean likeComment(String memberId, Integer commentNo); // true = liked, false = unliked
    boolean isCommentLiked(String memberId, Integer commentNo);
    long getLikeCount(Integer commentNo); // 좋아요 수 조회
}