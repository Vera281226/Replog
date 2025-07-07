package pack.modules.genres.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pack.modules.genres.model.Genres;

import java.util.Optional;

/**
 * Genres 엔티티에 대한 DB 접근 인터페이스
 */
@Repository
public interface GenresRepository extends JpaRepository<Genres, Integer> {

    // ✅ 장르 이름으로 조회 (서비스 필터링 시 사용)
    Optional<Genres> findByName(String name);
}
