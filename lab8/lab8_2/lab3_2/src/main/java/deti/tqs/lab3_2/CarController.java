package deti.tqs.lab3_2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {
    
    private final CarManagerService carManagerService;

    public CarController(CarManagerService carManagerService) {
        this.carManagerService = carManagerService;
    }

    @PostMapping("/cars")
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        return new ResponseEntity<>(carManagerService.save(car), HttpStatus.CREATED);
    }

    @GetMapping("/cars")
    public List<Car> getAllCars() {
        return carManagerService.getAllCars();
    }

    @GetMapping("/cars/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable(value = "id") Long carId) {
        return carManagerService.getCarDetails(carId)
                .map(car -> ResponseEntity.ok().body(car))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // NEW ENDPOINT: Find replacement car
    @GetMapping("/cars/{id}/replacement")
    public ResponseEntity<Car> findReplacement(@PathVariable(value = "id") Long carId) {
        return carManagerService.findReplacement(carId)
                .map(car -> ResponseEntity.ok().body(car))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // NEW ENDPOINT: Update car availability
    @PutMapping("/cars/{id}/availability")
    public ResponseEntity<Car> updateAvailability(
            @PathVariable(value = "id") Long carId,
            @RequestParam boolean available) {
        
        return carManagerService.getCarDetails(carId)
                .map(car -> {
                    car.setAvailable(available);
                    return ResponseEntity.ok().body(carManagerService.save(car));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // NEW ENDPOINT: Delete car
    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable(value = "id") Long carId) {
        return carManagerService.getCarDetails(carId)
                .map(car -> {
                    carManagerService.deleteCar(carId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // NEW ENDPOINT: Get cars by category
    @GetMapping("/cars/category/{category}")
    public ResponseEntity<List<Car>> getCarsByCategory(@PathVariable String category) {
        List<Car> cars = carManagerService.getCarsByCategory(category);
        
        if (cars.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok().body(cars);
    }
}
