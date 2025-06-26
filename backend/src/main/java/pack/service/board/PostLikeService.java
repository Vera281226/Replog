package pack.service.board;

public interface PostLikeService {
    boolean likePost(String id, Integer postNo); // true: liked, false: unliked
    boolean isPostLiked(String id, Integer postNo);
}