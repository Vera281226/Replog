package pack.repository.member;

import pack.model.member.EmailCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAuthRepository extends JpaRepository<EmailCode, Long> {
    Optional<EmailCode> findByEmail(String email);
}
