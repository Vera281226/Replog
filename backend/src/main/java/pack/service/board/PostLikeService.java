package pack.service.board;

public interface PostLikeService {
    boolean likePost(String memberId, Integer postNo); // true: liked, false: unliked
    boolean isPostLiked(String memberId, Integer postNo);
}