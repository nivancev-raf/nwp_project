package rs.raf.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.backend.model.ErrorMessage;
import rs.raf.backend.model.User;


public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {
    Page<ErrorMessage> findByOrderCreatedBy(User user, Pageable pageable);
}
