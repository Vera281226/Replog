package pack.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.board.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	List<Comment> findByPostNoOrderByCreatedAtAsc(Integer postNo);
}