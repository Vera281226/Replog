package pack.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.member.Genre;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(String name);
}
