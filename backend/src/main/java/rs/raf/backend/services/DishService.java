package rs.raf.backend.services;

import org.springframework.stereotype.Service;
import rs.raf.backend.model.Dish;
import rs.raf.backend.repository.DishRepository;

import java.util.List;
import java.util.Optional;

@Service
//@Transactional
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id);
    }
}