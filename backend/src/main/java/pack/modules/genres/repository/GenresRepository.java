package pack.modules.genres.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.modules.genres.model.Genres;

/**
 * Genres 엔티티에 대한 DB 접근 인터페이스
 */
public interface GenresRepository extends JpaRepository<Genres, Integer> {
}
