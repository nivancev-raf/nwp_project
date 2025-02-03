package rs.raf.backend.services;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.raf.backend.model.Order;
import rs.raf.backend.model.OrderStatus;
import rs.raf.backend.repository.OrderRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@Transactional // Ova anotacija označava da se transakcija automatski zatvara nakon izvršenja metode
public class OrderProcessingService {
    private final OrderRepository orderRepository;
    private final Random random = new Random();

    public OrderProcessingService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedDelay = 1000) // Proverava na svaku sekundu
    public void processOrders() {
        processOrderedToPreparingTransition();
        processPreparingToInDeliveryTransition();
        processInDeliveryToDeliveredTransition();
    }


    private void processOrderedToPreparingTransition() {
        List<Order> orderedOrders = orderRepository.findByStatusAndActive(OrderStatus.ORDERED, true);

        for (Order order : orderedOrders) {
            if (Duration.between(order.getCreatedAt(), LocalDateTime.now()).getSeconds() >= 10) {
                if (random.nextInt(5) == 0) { // 20% šansa za procesiranje u svakom ciklusu
                    order.setStatus(OrderStatus.PREPARING);
                    orderRepository.save(order);
                }
            }
        }
    }

    private void processPreparingToInDeliveryTransition() {
        List<Order> preparingOrders = orderRepository.findByStatusAndActive(OrderStatus.PREPARING, true);

        for (Order order : preparingOrders) {
            if (Duration.between(order.getLastStatusChange(), LocalDateTime.now()).getSeconds() >= 15) {
                if (random.nextInt(5) == 0) {
                    order.setStatus(OrderStatus.IN_DELIVERY);
//                    order.setLastStatusChange(LocalDateTime.now());
                    orderRepository.save(order);
                }
            }
        }
    }

    private void processInDeliveryToDeliveredTransition() {
        List<Order> inDeliveryOrders = orderRepository.findByStatusAndActive(OrderStatus.IN_DELIVERY, true);

        for (Order order : inDeliveryOrders) {
            if (Duration.between(order.getLastStatusChange(), LocalDateTime.now()).getSeconds() >= 20) {
                if (random.nextInt(5) == 0) {
                    order.setStatus(OrderStatus.DELIVERED);
//                    order.setLastStatusChange(LocalDateTime.now());
                    orderRepository.save(order);
                }
            }
        }
    }
}
