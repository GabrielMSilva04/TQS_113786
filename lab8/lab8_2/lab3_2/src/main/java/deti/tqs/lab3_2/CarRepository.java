package deti.tqs.lab3_2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Car findByCarId(Long carId);
    
    @Override
    @NonNull
    List<Car> findAll();
    
    List<Car> findByCategoryAndAvailableAndCarIdNot(String category, boolean available, Long carId);
    
    // NEW METHOD: Find cars by category
    List<Car> findByCategory(String category);
}
