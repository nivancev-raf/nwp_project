package rs.raf.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.raf.backend.model.Order;
import rs.raf.backend.model.OrderStatus;
import rs.raf.backend.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAll();

    int countByStatusInAndActiveTrue(List<OrderStatus> statuses);
    List<Order> findByStatusAndActive(OrderStatus status, boolean active);

    // za search
    List<Order> findByCreatedBy(User user);
    List<Order> findByStatusIn(List<OrderStatus> statuses);
    List<Order> findByCreatedById(Long userId);
    List<Order> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    List<Order> findByStatusInAndCreatedById(List<OrderStatus> statuses, Long userId);
    List<Order> findByStatusInAndActiveTrue(List<OrderStatus> statuses);
    List<Order> findByStatusInAndCreatedAtBetween(List<OrderStatus> statuses, LocalDateTime from, LocalDateTime to);
    List<Order> findByCreatedAtBetweenAndCreatedById(LocalDateTime from, LocalDateTime to, Long userId);
    List<Order> findByStatusInAndCreatedAtBetweenAndCreatedById(
            List<OrderStatus> statuses,
            LocalDateTime from,
            LocalDateTime to,
            Long userId
    );
    List<Order> findByScheduledTimeLessThanEqualAndStatusIsNull(LocalDateTime now);
    // findByScheduledTimeGreaterThanEqualAndStatusIsNull
    List<Order> findByScheduledTimeGreaterThanEqualAndStatusIsNull(LocalDateTime now);
    // findByScheduledTimeGreaterThanEqualAndStatusEquals
    List<Order> findByScheduledTimeGreaterThanEqualAndStatusEquals(LocalDateTime now, OrderStatus status);
    // findByScheduledTimeLessThanEqualAndStatusEquals
    List<Order> findByScheduledTimeLessThanEqualAndStatusEquals(LocalDateTime now, OrderStatus status);
}
