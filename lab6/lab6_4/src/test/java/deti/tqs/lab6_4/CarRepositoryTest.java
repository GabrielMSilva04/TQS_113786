package deti.tqs.lab6_4;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CarRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarRepository carRepository;

    @Test
    void whenFindCarById_thenReturnCar() {
        Car car = new Car("Tesla", "Model X", "LUXURY");
        entityManager.persistAndFlush(car);

        Car found = carRepository.findByCarId(car.getCarId());
        assertThat(found).isNotNull();
        assertThat(found.getMaker()).isEqualTo(car.getMaker());
        assertThat(found.getModel()).isEqualTo(car.getModel());
    }

    @Test
    void whenInvalidId_thenReturnNull() {
        Car fromDb = carRepository.findByCarId(99L);
        assertThat(fromDb).isNull();
    }

    @Test
    void givenSetOfCars_whenFindAll_thenReturnAllCars() {
        Car tesla = new Car("Tesla", "Model X", "LUXURY");
        Car toyota = new Car("Toyota", "Prius", "COMPACT");
        Car honda = new Car("Honda", "Civic", "COMPACT");

        entityManager.persist(tesla);
        entityManager.persist(toyota);
        entityManager.persist(honda);
        entityManager.flush();

        List<Car> allCars = carRepository.findAll();

        assertThat(allCars)
            .hasSize(3)
            .extracting(Car::getMaker)
            .containsExactlyInAnyOrder(tesla.getMaker(), toyota.getMaker(), honda.getMaker());
    }

    @Test
    void whenFindReplacement_thenReturnMatchingCar() {
        Car originalCar = new Car("Tesla", "Model 3", "LUXURY");
        Car replacementCar = new Car("BMW", "7 Series", "LUXURY");
        replacementCar.setAvailable(true);
        Car unavailableCar = new Car("Mercedes", "S Class", "LUXURY");
        unavailableCar.setAvailable(false);

        entityManager.persist(originalCar);
        entityManager.persist(replacementCar);
        entityManager.persist(unavailableCar);
        entityManager.flush();

        List<Car> replacements = carRepository.findByCategoryAndAvailableAndCarIdNot(
            "LUXURY", 
            true, 
            originalCar.getCarId()
        );

        assertThat(replacements)
            .hasSize(1)
            .extracting(Car::getMaker)
            .containsOnly("BMW");
    }
}
