package pack.service.board;

public interface CommentLikeService {
    boolean likeComment(String memberId, Integer commentNo); // true = liked, false = unliked
    boolean isLiked(String memberId, Integer commentNo);
}
