package deti.tqs.lab3_2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarManagerService carService;

    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car("Tesla", "Model X");
        car.setCarId(1L);
    }

    @Test
    void whenSaveCar_thenReturnCar() {
        when(carRepository.save(any())).thenReturn(car);

        Car created = carService.save(car);

        assertThat(created).isNotNull();
        assertThat(created.getMaker()).isEqualTo("Tesla");
        verify(carRepository).save(any());
    }

    @Test
    void whenGetAllCars_thenReturnAllCars() {
        Car car2 = new Car("Toyota", "Prius");
        car2.setCarId(2L);
        List<Car> allCars = Arrays.asList(car, car2);

        when(carRepository.findAll()).thenReturn(allCars);

        List<Car> found = carService.getAllCars();
        
        assertThat(found).hasSize(2).extracting(Car::getMaker).contains(car.getMaker(), car2.getMaker());
        verify(carRepository).findAll();
    }

    @Test
    void whenGetCarById_thenReturnCar() {
        when(carRepository.findByCarId(1L)).thenReturn(car);

        Optional<Car> found = carService.getCarDetails(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getMaker()).isEqualTo(car.getMaker());
        verify(carRepository).findByCarId(1L);
    }

    @Test
    void whenGetCarByInvalidId_thenReturnEmpty() {
        when(carRepository.findByCarId(99L)).thenReturn(null);

        Optional<Car> found = carService.getCarDetails(99L);

        assertThat(found).isEmpty();
        verify(carRepository).findByCarId(99L);
    }
}
