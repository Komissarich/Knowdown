FROM maven:3.9-eclipse-temurin-23 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN echo "Содержимое target:" && ls -la target/ || echo "Папка target пустая!"
RUN mvn clean package -DskipTests

FROM eclipse-temurin:23
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]