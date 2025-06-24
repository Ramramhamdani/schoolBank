# First stage: Build JAR with Maven and Temurin JDK
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /build
COPY backend/pom.xml ./
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Second stage: Use distroless for secure runtime
FROM gcr.io/distroless/java17-debian11
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
