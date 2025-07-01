package pack.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.board.PostLike;
import pack.model.board.PostLikeId;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    boolean existsByMemberIdAndPostNo(String memberId, Integer postNo);
    void deleteByMemberIdAndPostNo(String memberId, Integer postNo);
}