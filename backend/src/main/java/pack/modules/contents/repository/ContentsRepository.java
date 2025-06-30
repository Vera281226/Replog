package pack.modules.contents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pack.modules.contents.model.Contents;

import java.util.Optional;

@Repository
public interface ContentsRepository extends JpaRepository<Contents, Integer> {

    /**
     * TMDB ID 기준 콘텐츠 조회
     * - TMDB 연동 시 중복 방지에 사용
     */
    Optional<Contents> findByTmdbId(Integer tmdbId);
}
