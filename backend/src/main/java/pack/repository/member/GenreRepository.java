package pack.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.member.Genre;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
}