package deti.tqs.lab6_1;

import org.junit.jupiter.api.Test;
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
class CustomerRepositoryIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("testuser")
            .withPassword("testpass")
            .withDatabaseName("testdb");

    @Autowired
    private CustomerRepository customerRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Test
    void testInsertAndRetrieveCustomer() {
        // Insert a customer
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customerRepository.save(customer);

        // Retrieve the customer
        Customer retrievedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(retrievedCustomer.getName()).isEqualTo("John Doe");
        assertThat(retrievedCustomer.getEmail()).isEqualTo("john.doe@example.com");
    }
}