package rs.raf.backend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.raf.backend.model.Dish;
import rs.raf.backend.model.User;
import rs.raf.backend.model.UserTypes;
import rs.raf.backend.repository.DishRepository;
import rs.raf.backend.repository.UserRepository;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DishRepository dishRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, DishRepository dishRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.dishRepository = dishRepository;
    }

    private void loadDishes() {
        Dish dish1 = new Dish();
        dish1.setName("Chicken");
        dish1.setPrice(599.99);

        Dish dish2 = new Dish();
        dish2.setName("Pizza");
        dish2.setPrice(299.99);

        Dish dish3 = new Dish();
        dish3.setName("Steak");
        dish3.setPrice(999.99);

        this.dishRepository.save(dish1);
        this.dishRepository.save(dish2);
        this.dishRepository.save(dish3);
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@admin.com") == null) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setEmail("admin@admin.com");
            admin.setRole(UserTypes.ADMIN);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setPermissions(Set.of(
                    "can_create_users",
                    "can_read_users",
                    "can_update_users",
                    "can_delete_users"
            ));
            userRepository.save(admin);
        }
            /*
            *   (1, 'can_search_order'),
                (1, 'can_place_order'),
                (1, 'can_cancel_order'),
                (1, 'can_track_order'),
                (1, 'can_schedule_order');
            * */

        // Load dishes if there are none
        if (dishRepository.count() == 0) {
            loadDishes();
        }
    }
}