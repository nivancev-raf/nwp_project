package rs.raf.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.backend.model.ErrorMessage;


public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {
}
