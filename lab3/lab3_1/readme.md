**a) Identify a couple of examples that use AssertJ expressive methods chaining.**

1. In A_EmployeeRepositoryTest.java:
```
List<Employee> allEmployees = employeeRepository.findAll();
assertThat(allEmployees).hasSize(3).extracting(Employee::getName).containsOnly(alex.getName(), ron.getName(), bob.getName());
```

2. Another example from the same file:
```
assertThat(results)
    .hasSize(2)
    .extracting(Employee::getEmail)
    .containsExactlyInAnyOrder(
        "bob@ua.pt",
        "ron@ua.pt"
    );
```

**b) Take note of transitive annotations included in @DataJpaTest.**
Here are the key transitive annotations included in @DataJpaTest:

1. @Transactional - Makes each test method run in a transaction
2. @AutoConfigureTestDatabase - Sets up an in-memory test database
3. @ExtendWith(SpringExtension.class) - Enables Spring testing support
4. @BootstrapWith(SpringBootTestContextBootstrapper.class) - Bootstraps the Spring test context

These annotations are automatically included when using @DataJpaTest, which is why we can see them in action in A_EmployeeRepositoryTest.java without explicitly declaring them


**c) Identify an example in which you mock the behavior of the repository (and avoid involving a database).**

An example of mocking repository behavior without involving a database can be found in B_EmployeeService_UnitTest.java.

```
Mockito.when(employeeRepository.findByName(john.getName())).thenReturn(john);
Mockito.when(employeeRepository.findByName(alex.getName())).thenReturn(alex);
Mockito.when(employeeRepository.findByName("wrong_name")).thenReturn(null);
Mockito.when(employeeRepository.findById(john.getId())).thenReturn(Optional.of(john));
Mockito.when(employeeRepository.findAll()).thenReturn(allEmployees);
Mockito.when(employeeRepository.findById(-99L)).thenReturn(Optional.empty());
```

**d) What is the difference between standard @Mock and @MockBean?**

both are used to mock dependencies, @mock comes from Mockito, @MockBean is a SpringBoot annotation, used to replace a actual Spring bean with a mock

**e) What is the role of the file “application-integrationtest.properties”? In which conditions will it be used?**

* It contains configuration for connecting to a real MySQL database instead of the in-memory database
* It will be used when the @TestPropertySource annotation is enabled in integration tests

```
// adapt AutoConfigureTestDatabase with TestPropertySource to use a real database
// @TestPropertySource(locations = "application-integrationtest.properties")
```

**f) the sample project demonstrates three test strategies to assess an API (C, D and E) developed with SpringBoot. Which are the main/key differences?**

1. C_EmployeeController_WithMockServiceTest:
* Uses @WebMvcTest
* Mocks the service layer
* Tests only the web layer in isolation
* Uses MockMvc for HTTP requests

2. D_EmployeeRestControllerIT:
* Uses @SpringBootTest(webEnvironment = WebEnvironment.MOCK)
* Full Spring context but with mock web environment
* Uses real repository with test database
* Uses MockMvc for HTTP requests

3. E_EmployeeRestControllerTemplateIT:
* Uses @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
* Full Spring context with real web server
* Uses real repository with test database
* Uses TestRestTemplate for HTTP requests

The progression from C to E shows increasing levels of integration, from isolated controller testing to full end-to-end testing.