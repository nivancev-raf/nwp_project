package rs.raf.backend.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    private List<Long> dishes;
    private LocalDateTime scheduledTime;


    public List<Long> getDishes() {
        return dishes;
    }

    public void setDishes(List<Long> dishes) {
        this.dishes = dishes;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}
