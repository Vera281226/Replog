package pack.repository.board; 

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import pack.model.board.Post;

public interface PostRepository extends JpaRepository<Post, Integer>, PagingAndSortingRepository<Post, Integer> {

    // 카테고리 필터만
    Page<Post> findByCategory(String category, Pageable pageable);

    // 검색 + 카테고리
    Page<Post> findByTitleContainingAndCategory(String title, String category, Pageable pageable);
    Page<Post> findByContentContainingAndCategory(String content, String category, Pageable pageable);
    Page<Post> findByNicknameContainingAndCategory(String nickname, String category, Pageable pageable);

    // 검색만
    Page<Post> findByTitleContaining(String title, Pageable pageable);
    Page<Post> findByContentContaining(String content, Pageable pageable);
    Page<Post> findByNicknameContaining(String nickname, Pageable pageable);
}