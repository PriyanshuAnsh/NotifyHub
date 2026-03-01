# ---- Build Stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom first (cache dependencies)
COPY notifyhub/pom.xml ./pom.xml
RUN mvn -q -B dependency:go-offline

# Copy sources
COPY notifyhub/src ./src

# Build jar
RUN mvn -q -DskipTests clean package


# ---- Runtime Stage ----
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]