package rs.raf.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.raf.backend.model.Dish;
import rs.raf.backend.services.DishService;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
@CrossOrigin
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public List<Dish> getAllDishes() {
        return dishService.findAll();
    }

//    @PostMapping
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public Dish createDish(@RequestBody Dish dish) {
//        return dishService.save(dish);
//    }
//
//    @GetMapping("/{id}")
//    public Dish getDishById(@PathVariable Long id) {
//        return dishService.findById(id)
//                .orElseThrow(() -> new RuntimeException("Dish not found"));
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public void deleteDish(@PathVariable Long id) {
//        dishService.deleteById(id);
//    }
}