# Stage 1
FROM maven:3-amazoncorretto-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests --file pom.xml

# Stage 2
FROM amazoncorretto:17-alpine3.19-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar e-commerce.jar

ENTRYPOINT ["java", "-jar", "/app/e-commerce.jar"]