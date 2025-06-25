package pack.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.board.PostLike;
import pack.model.board.PostLikeId;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    Optional<PostLike> findByIdAndPostNo(String id, Integer postNo);
    boolean existsByIdAndPostNo(String id, Integer postNo);
    void deleteByIdAndPostNo(String id, Integer postNo);
}