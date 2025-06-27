package pack.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.model.member.EmailCode;

import java.util.Optional;

public interface EmailCodeRepository extends JpaRepository<EmailCode, Long> {
    Optional<EmailCode> findByEmail(String email);
}
