package deti.tqs.lab3_2;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarManagerService {
    
    private final CarRepository carRepository;

    public CarManagerService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarDetails(Long carId) {
        Car car = carRepository.findByCarId(carId);
        return Optional.ofNullable(car);
    }

    public Optional<Car> findReplacement(Long carId) {
        Car car = carRepository.findByCarId(carId);
        if (car == null) {
            return Optional.empty();
        }

        List<Car> replacements = carRepository.findByCategoryAndAvailableAndCarIdNot(
            car.getCategory(), 
            true, 
            carId
        );

        return replacements.stream().findFirst();
    }
    
    // NEW METHOD: Delete car
    public void deleteCar(Long carId) {
        carRepository.deleteById(carId);
    }
    
    // NEW METHOD: Get cars by category
    public List<Car> getCarsByCategory(String category) {
        return carRepository.findByCategory(category);
    }
}
