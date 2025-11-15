ARG SERVICE_NAME

FROM maven:3.9-eclipse-temurin-21 AS build
ARG SERVICE_NAME
WORKDIR /app

COPY pom.xml .

COPY "${SERVICE_NAME}/pom.xml" "${SERVICE_NAME}/"

WORKDIR /app/${SERVICE_NAME}

RUN mvn dependency:go-offline

COPY "${SERVICE_NAME}/src" "src"

RUN mvn clean install -Dmaven.test.skip=true

# --- Final Image ---
FROM eclipse-temurin:21-jre-alpine
ARG SERVICE_NAME
WORKDIR /app

COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
