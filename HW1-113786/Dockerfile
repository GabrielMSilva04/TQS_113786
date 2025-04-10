# Use Maven to build the application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
# RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
COPY .env ./src/main/resources/.env
RUN mvn clean package -DskipTests
RUN ls -la target/ 

# Use Java 21 runtime to run the application
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY .env /app/.env
ENTRYPOINT ["java", "-jar", "app.jar"]