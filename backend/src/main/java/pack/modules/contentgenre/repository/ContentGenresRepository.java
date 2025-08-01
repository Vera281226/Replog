package pack.modules.contentgenre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pack.modules.contentgenre.model.ContentGenres;
import pack.modules.contentgenre.model.ContentGenresId;

import java.util.List;

/**
 * 콘텐츠-장르 매핑 Repository
 * 복합키(ContentGenresId)를 사용한 JPA 인터페이스
 */
public interface ContentGenresRepository extends JpaRepository<ContentGenres, ContentGenresId> {

	@Query("""
		    SELECT g.name FROM ContentGenres cg
		    JOIN Genres g ON cg.genreId = g.genreId
		    WHERE cg.contentId = :contentId
		    """)
		    List<String> findGenreNamesByContentId(@Param("contentId") Integer contentId);

	boolean existsByContentIdAndGenreId(Integer contentId, Integer genreId);
	
	
}
