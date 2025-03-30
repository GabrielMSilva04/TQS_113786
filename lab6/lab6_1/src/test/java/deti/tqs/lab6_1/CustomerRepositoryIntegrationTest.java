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
class CustomerRepositoryIntegrationTest {

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Test
    @Order(1)
    void testInsertCustomer() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customerRepository.save(customer);

        customerId = customer.getId();
        assertThat(customerId).isNotNull();
    }

    @Test
    @Order(2)
    void testRetrieveCustomer() {
        Customer retrievedCustomer = customerRepository.findById(customerId).orElseThrow();
        assertThat(retrievedCustomer.getName()).isEqualTo("John Doe");
        assertThat(retrievedCustomer.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @Order(3)
    void testUpdateCustomer() {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        customer.setName("Jane Doe");
        customerRepository.save(customer);

        Customer updatedCustomer = customerRepository.findById(customerId).orElseThrow();
        assertThat(updatedCustomer.getName()).isEqualTo("Jane Doe");
    }

    @Test
    @Order(4)
    void testDeleteCustomer() {
        customerRepository.deleteById(customerId);
        assertThat(customerRepository.findById(customerId)).isNotPresent();
    }
}