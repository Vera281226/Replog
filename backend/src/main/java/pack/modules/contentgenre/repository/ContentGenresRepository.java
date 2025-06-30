package pack.modules.contentgenre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.modules.contentgenre.model.ContentGenres;
import pack.modules.contentgenre.model.ContentGenresId;

/**
 * 콘텐츠-장르 매핑 Repository
 * 복합키(ContentGenresId)를 사용한 JPA 인터페이스
 */
public interface ContentGenresRepository extends JpaRepository<ContentGenres, ContentGenresId> {

}
