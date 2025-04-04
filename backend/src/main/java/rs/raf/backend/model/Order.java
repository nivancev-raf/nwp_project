package rs.raf.backend.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // EnumType.STRING znaci da ce se cuvati kao string u bazi
    private OrderStatus status;

    @ManyToOne // jedan user moze da napravi vise ordera
    private User createdBy;

    @Column
    private Boolean active; // order aktivan ili ne

    @ManyToMany // vise ordera moze da ima vise jela
    @JoinTable(
            name = "order_dishes",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private List<Dish> dishes;

    @Column
    private LocalDateTime scheduledTime; // vreme kada je zakazan order

    @Column
    private LocalDateTime createdAt; // vreme kada je kreiran order

    @Column
    private LocalDateTime lastStatusChange; // vreme kada je poslednji put promenjen status ordera

    @Version
    @Column(name = "version")
    private Long version;

/*
OPTIMISTIC LOCK:
// Optimistic lock je mehanizam koji se koristi da bi se izbegli konflikti prilikom
// istovremenog pristupa podacima od strane više korisnika.
// U ovom slučaju, koristimo @Version anotaciju da bismo obezbedili da se verzija
// entiteta automatski povećava svaki put kada se entitet sačuva.

// Version = 0
val order = orderRepository.findById(1).get()

// Neko drugi u međuvremenu menja isti order
val order2 = orderRepository.findById(1).get()
order2.status = OrderStatus.CANCELED
orderRepository.save(order2)  // Version = 1

// Pokušaj da se sačuva prva verzija će fail-ovati
order.status = OrderStatus.DELIVERED
orderRepository.save(order)  // Throws OptimisticLockException jer je version već 1
 */



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        this.lastStatusChange = LocalDateTime.now();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastStatusChange() {
        return lastStatusChange;
    }

    public void setLastStatusChange(LocalDateTime lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }
}