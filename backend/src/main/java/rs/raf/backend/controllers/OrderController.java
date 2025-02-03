package rs.raf.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.raf.backend.model.CreateOrderRequest;
import rs.raf.backend.model.Order;
import rs.raf.backend.model.User;
import rs.raf.backend.services.OrderService;
import rs.raf.backend.services.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;


    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }


//    @PreAuthorize("hasAuthority('can_place_order')")
//    @PostMapping
//    public Order placeOrder(@RequestBody CreateOrderRequest order) {
//        try {
//            String username = SecurityContextHolder.getContext().getAuthentication().getName();
//            return orderService.placeOrder(order, userService.findByEmail(username));
//        } catch (Exception e) {
//            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }


//    @GetMapping
//    @PreAuthorize("hasAuthority('can_search_order')")
//    public List<Order> searchOrders(
//            @RequestParam(required = false) List<String> status,
//            @RequestParam(required = false) String dateFrom,
//            @RequestParam(required = false) String dateTo,
//            @RequestParam(required = false) Long userId) {
//
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User currentUser = userService.findByEmail(username);
//
//        return orderService.search(status, dateFrom, dateTo, userId, currentUser);
//    }

    @GetMapping
    @PreAuthorize("hasAuthority('can_search_order')")
    public List<Order> searchOrders(
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) Long userId) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(username);

        return orderService.search(status, dateFrom, dateTo, userId, currentUser);
    }

    @PreAuthorize("hasAuthority('can_place_order')")
    @PostMapping
    public Order placeOrder(@RequestBody CreateOrderRequest order) {
        try {
//            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return orderService.placeOrder(order);
        } catch (Exception e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Error while placing order, look 'Error message' table for more information");
        }
    }

    @PreAuthorize("hasAuthority('can_track_order')")
    @GetMapping("/track/{id}")
    public Order trackOrder(@PathVariable Long id) {
        return orderService.trackOrder(id);
    }

    @PreAuthorize("hasAuthority('can_cancel_order')")
    @DeleteMapping("/{id}")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }
}
