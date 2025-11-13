# Stage 1: Build the application
# Use a Maven image to build the .jar file
FROM maven:3.8.5-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code and build the package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final, small runtime image
# Use a slim JRE for a smaller, more secure image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the built .jar file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (you'll still map it in docker-compose)
# 8888 for config, 8761 for eureka, 8080 for gateway, etc.
# This line is good practice but the port mapping is what matters.
# We will define the real ports in docker-compose.

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]