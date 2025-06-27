package pack.repository.theater;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pack.model.theater.Theater;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Integer> {
}