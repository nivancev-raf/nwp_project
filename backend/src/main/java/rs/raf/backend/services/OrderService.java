package rs.raf.backend.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.raf.backend.model.*;
import rs.raf.backend.repository.DishRepository;
import rs.raf.backend.repository.ErrorMessageRepository;
import rs.raf.backend.repository.OrderRepository;
import rs.raf.backend.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final ErrorMessageRepository errorMessageRepository;

    private final UserService userService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        DishRepository dishRepository,
                        UserService userService,
                        ErrorMessageRepository errorMessageRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
        this.userService = userService;
        this.errorMessageRepository = errorMessageRepository;
    }

    public List<Order> search(List<String> statuses, LocalDateTime dateFrom,
                              LocalDateTime dateTo, Long userId, User currentUser) {

        // ako je user obican, vrati sve njegove porudzbine
        if (!currentUser.getRole().equals(UserTypes.ADMIN)) {
            return orderRepository.findByCreatedBy(currentUser);
        }

        // ako je user admin, vrati porudzbine prema parametrima
        if (statuses != null && !statuses.isEmpty()) {
            List<OrderStatus> orderStatuses = statuses.stream()
                    .map(OrderStatus::valueOf)
                    .collect(Collectors.toList());

            if (dateFrom != null && dateTo != null) {
                if (userId != null) {
                    // ima sve parametre
                    return orderRepository.findByStatusInAndCreatedAtBetweenAndCreatedById(
                            orderStatuses, dateFrom, dateTo, userId);
                }
                // ima statuse i datume ali nema usera
                return orderRepository.findByStatusInAndCreatedAtBetween(orderStatuses, dateFrom, dateTo);
            }

            if (userId != null) {
                // ima statuse i usera ali nema datuma
                return orderRepository.findByStatusInAndCreatedById(orderStatuses, userId);
            }

            // ima statuse ali nema datuma i usera
            return orderRepository.findByStatusIn(orderStatuses);
        }

        if (dateFrom != null && dateTo != null) {
            if (userId != null) {
                // ima datume i usera ali nema statuse
                return orderRepository.findByCreatedAtBetweenAndCreatedById(dateFrom, dateTo, userId);
            }
            // ima datume ali nema statuse i usera
            return orderRepository.findByCreatedAtBetween(dateFrom, dateTo);
        }

        if (userId != null) {
            // ima usera ali nema statuse i datume
            return orderRepository.findByCreatedById(userId);
        }

        // nema parametara
        return orderRepository.findAll();
    }

    public Order placeOrder(CreateOrderRequest req) {

        // provera broja aktivnih porudžbina u pripremi/dostavi (stanja PREPARING i IN_DELIVERY)
        int activeOrdersCount = orderRepository.countByStatusInAndActiveTrue(List.of(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY));

        Order order = new Order();
        order.setStatus(OrderStatus.ORDERED);
        order.setActive(true);
        order.setCreatedAt(LocalDateTime.now());
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("USER EMAIL:" + userEmail);
        order.setCreatedBy(userService.findByEmail(userEmail));
        order.setDishes(dishRepository.findAllById(req.getDishes()));

        Order createdOrder = orderRepository.save(order);

        if (activeOrdersCount >= 3) {
            System.out.println("pravim error message");
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setDate(LocalDateTime.now());
            errorMessage.setOrder(createdOrder);
            errorMessage.setOperation("PLACE_ORDER");
            errorMessage.setMessage("Maximum number of concurrent orders (3) reached");
            errorMessageRepository.save(errorMessage);
        }

        return createdOrder;
    }

    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.ORDERED) {
            throw new IllegalStateException("Only orders in ORDERED status can be canceled");
        }

        order.setStatus(OrderStatus.CANCELED);
        order.setActive(false);
        orderRepository.save(order);
    }

    public Order trackOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order scheduleOrder(CreateOrderRequest request, User user) {
        // TODO: Implementirati zakazivanje porudžbine
        Order order = new Order();
        order.setCreatedBy(user);
        order.setDishes(dishRepository.findAllById(request.getDishes()));
        order.setScheduledTime(request.getScheduledTime());
        System.out.println("Scheduled time: " + request.getScheduledTime());
        order.setCreatedAt(LocalDateTime.now());
        order.setActive(true);
        order.setStatus(OrderStatus.SCHEDULED);


        return orderRepository.save(order);
    }

    @Scheduled(fixedDelay = 3000)
    public void processScheduledOrders() {

        // fetches all orders that are scheduled for now or before now, status is null (not ordered)
        LocalDateTime nowMinusOneHour = LocalDateTime.now().minusHours(1);
        System.out.println("Now minus one hour: " + nowMinusOneHour);
        List<Order> scheduledOrders = orderRepository.findByScheduledTimeLessThanEqualAndStatusEquals(nowMinusOneHour, OrderStatus.SCHEDULED);
        // print scheduled orders
        System.out.println("Scheduled orders: " + scheduledOrders);


        for (Order order : scheduledOrders) {
            try {
                // check if there are more than 3 active orders
                long activeOrders = orderRepository.countByStatusInAndActiveTrue(
                        Arrays.asList(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY)
                );

                if (activeOrders >= 3) {
                    saveError(order, "Maximum concurrent orders limit reached (3)");
                    continue;
                }

                order.setStatus(OrderStatus.ORDERED);
                orderRepository.save(order);
            } catch (Exception e) {
                saveError(order, e.getMessage());
            }
        }

        List<Order> activeOrders = orderRepository.findByStatusInAndActiveTrue(
                Arrays.asList(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY)
        );
        if (activeOrders.size() > 3) {
            System.out.println("Active orders count: " + activeOrders.size());
            // get order with most recent created at and cancel it
            // .min -> returns the smallest element based on the comparator
            Order orderToCancel = activeOrders.stream().min((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                    .orElseThrow(() -> new RuntimeException("No active orders found"));
            // now cancel that order, make active false and status canceled, and call saveError with it
            orderToCancel.setActive(false);
            orderToCancel.setStatus(OrderStatus.CANCELED);
            orderRepository.save(orderToCancel);
            saveError(orderToCancel, "Maximum concurrent orders limit reached (3)");
        }

    }

    private void saveError(Order order, String message) {
        ErrorMessage error = new ErrorMessage();
        error.setDate(LocalDateTime.now());
        error.setOperation("SCHEDULE");
        error.setMessage(message);
        error.setOrder(order);
        errorMessageRepository.save(error);
    }
}