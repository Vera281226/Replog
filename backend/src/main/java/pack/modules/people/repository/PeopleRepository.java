package pack.modules.people.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.modules.people.model.People;

import java.util.Optional;

/**
 * People 엔티티에 대한 데이터베이스 접근을 처리하는 JPA Repository 인터페이스입니다.
 * 기본 CRUD 메서드는 JpaRepository에서 자동으로 제공됩니다.
 */
public interface PeopleRepository extends JpaRepository<People, Integer> {

    /**
     * TMDB 고유 ID를 기준으로 People 엔티티를 조회합니다.
     * @param tmdbId TMDB에서 제공한 인물 고유 ID
     * @return 해당 ID를 가진 People 객체(Optional)
     */
    Optional<People> findByTmdbId(int tmdbId);
}
