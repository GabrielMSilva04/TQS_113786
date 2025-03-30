package deti.tqs.lab6_1;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerRepositoryFlywayTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("testuser")
            .withPassword("testpass")
            .withDatabaseName("testdb");

    @Autowired
    private CustomerRepository customerRepository;

    private static Long customerId;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Test
    @Order(1)
    void testRetrieveInitialData() {
        // Verify the sample data inserted by Flyway
        Customer customer1 = customerRepository.findById(1L).orElseThrow();
        assertThat(customer1.getName()).isEqualTo("John Doe");
        assertThat(customer1.getEmail()).isEqualTo("john.doe@example.com");

        Customer customer2 = customerRepository.findById(2L).orElseThrow();
        assertThat(customer2.getName()).isEqualTo("Jane Smith");
        assertThat(customer2.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    @Order(2)
    void testInsertCustomer() {
        Customer customer = new Customer();
        customer.setName("Alice Johnson");
        customer.setEmail("alice.johnson@example.com");
        customerRepository.save(customer);

        customerId = customer.getId();
        assertThat(customerId).isNotNull();
    }

    @Test
    @Order(3)
    void testRetrieveInsertedCustomer() {
        Customer retrievedCustomer = customerRepository.findById(customerId).orElseThrow();
        assertThat(retrievedCustomer.getName()).isEqualTo("Alice Johnson");
        assertThat(retrievedCustomer.getEmail()).isEqualTo("alice.johnson@example.com");
    }
}