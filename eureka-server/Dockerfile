ARG SERVICE_NAME

FROM maven:3.9-eclipse-temurin-21 AS build
ARG SERVICE_NAME
WORKDIR /app

# Copy full project (fastest, most stable)
COPY . .

# Build only the needed module
WORKDIR /app/${SERVICE_NAME}
RUN mvn -q clean package -DskipTests

# ---- Final Image ----
FROM eclipse-temurin:21-jre-alpine
ARG SERVICE_NAME
WORKDIR /app

COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
