package pack.modules.provides.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.modules.provides.model.Providers;

/**
 * Providers 테이블에 대한 JPA Repository
 */
public interface ProvidersRepository extends JpaRepository<Providers, Integer> {
    // 기본 CRUD 메서드 제공됨 (findAll, findById, save, deleteById 등)
}
