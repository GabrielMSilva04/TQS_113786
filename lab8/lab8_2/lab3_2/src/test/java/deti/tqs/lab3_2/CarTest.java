package deti.tqs.lab3_2;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CarTest {

    @Test
    void testCarConstructorAndGetters() {
        // Arrange & Act
        Car car = new Car("Tesla", "Model 3", "LUXURY");
        
        // Assert - using chained assertions
        assertThat(car)
            .extracting(Car::getMaker, Car::getModel, Car::getCategory, Car::isAvailable)
            .containsExactly("Tesla", "Model 3", "LUXURY", true);
    }
    
    @Test
    void testSetters() {
        // Arrange
        Car car = new Car();
        
        // Act
        car.setCarId(1L);
        car.setMaker("Toyota");
        car.setModel("Corolla");
        car.setCategory("COMPACT");
        car.setAvailable(false);
        
        // Assert - using chained assertions
        assertThat(car)
            .extracting(
                Car::getCarId,
                Car::getMaker,
                Car::getModel,
                Car::getCategory,
                Car::isAvailable
            )
            .containsExactly(1L, "Toyota", "Corolla", "COMPACT", false);
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Car car1 = new Car("BMW", "X5", "SUV");
        car1.setCarId(1L);
        
        Car car2 = new Car("Mercedes", "GLE", "SUV");
        car2.setCarId(1L);
        
        Car car3 = new Car("BMW", "X5", "SUV");
        car3.setCarId(2L);
        
        // Act & Assert - using soft assertions for equality checks
        assertThat(car1)
            .isEqualTo(car1)            // Same object
            .isEqualTo(car2)            // Different cars but same ID
            .isNotEqualTo(car3)         // Same car details but different ID
            .isNotEqualTo(null)         // Car vs null
            .isNotEqualTo(new Object()); // Car vs other object
        
        // HashCode tests
        assertThat(car1.hashCode())
            .isEqualTo(car2.hashCode())    // Same ID should have same hash
            .isNotEqualTo(car3.hashCode()); // Different ID should have different hash
        
        // Car with null ID
        Car carWithNullId = new Car("Audi", "A4", "SEDAN");
        assertThat(carWithNullId.hashCode()).isZero(); // Should be 0 for null ID
    }
    
    @Test
    void testToString() {
        // Arrange
        Car car = new Car("Honda", "Civic", "COMPACT");
        car.setCarId(5L);
        car.setAvailable(false);
        
        // Act
        String toString = car.toString();
        
        // Assert - using chained assertions
        assertThat(toString)
            .contains("carId=5")
            .contains("maker='Honda'")
            .contains("model='Civic'")
            .contains("category='COMPACT'")
            .contains("available=false");
    }
}