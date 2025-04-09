# Moliceiro Meals

Moliceiro Meals is a web application that connects diners with local restaurants in Aveiro, Portugal. The platform allows users to browse restaurants, view menus, and make reservations, while providing restaurant staff with tools to manage their offerings and bookings.

## Features

- **Restaurant Browsing**: View details about local Aveiro restaurants
- **Menu Management**: See daily and upcoming menus with detailed food items
- **Reservation System**: Make and manage restaurant reservations
- **Reservation Status Tracking**: Follow reservations through their lifecycle (pending → confirmed → completed)
- **Staff Dashboard**: Tools for restaurant managers to update menus and process reservations

## Technologies Used

- **Backend**: Spring Boot, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap
- **Testing**: JUnit 5, Mockito
- **CI/CD**: GitHub Actions
- **Code Quality**: SonarQube

## Quality Assurance Strategy

This project implements a comprehensive testing strategy:

1. **Unit Tests**: Testing individual components in isolation
2. **Integration Tests**: Verifying interactions between components
3. **Web Layer Tests**: Using MockMvc to test controllers
4. **End-to-End Tests**: Using Selenium WebDriver to test complete user flows
5. **Code Coverage**: Monitoring with JaCoCo
6. **Static Code Analysis**: Using SonarCloud quality gates

## Project Structure

The project follows a standard Spring Boot architecture:
- `config`: Application configuration including data initialization
- `model`: Domain entities (Restaurant, Menu, MenuItem, Reservation)
- `repository`: Data access layer
- `service`: Business logic
- `controller`: Web controllers for handling HTTP requests

## Example Data

This application includes a data initializer that will automatically load example restaurants and menus on first run.

### Sample Data Includes:
- 4 restaurants in Aveiro (Restaurant Do Moliceiro, A Salineira, Riavista, Cervejaria Rossio)
- Each restaurant has a menu for today
- Each restaurant has menus for the next 7 days
- Each menu includes appetizers, main courses, desserts, and beverages

### To Reset Example Data:
1. Stop the application
2. Delete the database or clear the tables
3. Restart the application

## Getting Started

1. Ensure you have the following prerequisites installed:
   - JDK 17 or higher
   - Maven 3.8+
   - MySQL (or H2 for development)

2. Clone the repository

3. Configure application properties:
- Edit `src/main/resources/application.properties` for database settings

4. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

5. Access the application at `http://localhost:8080`

## Project Structure

The project follows a standard Spring Boot architecture:
- `config`: Application configuration including data initialization
- `model`: Domain entities (Restaurant, Menu, MenuItem, Reservation)
- `repository`: Data access layer
- `service`: Business logic
- `controller`: Web controllers for handling HTTP requests
- `exception`: Custom exception handling
- `dto`: Data Transfer Objects
- `util`: Utility classes and helper methods

## API Documentation

The API is documented using SpringDoc OpenAPI. When the application is running, you can access:
- OpenAPI documentation: `http://localhost:8080/v3/api-docs`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Running Tests

To execute the full test suite:
```bash
mvn test
```