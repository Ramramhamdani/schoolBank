# First stage: Build JAR with Maven and Temurin JDK
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /build
COPY pom.xml backend/
COPY src backend/src
RUN mvn -f backend/pom.xml clean package -DskipTests

# Second stage: Use distroless for secure runtime
FROM gcr.io/distroless/java17-debian11
WORKDIR /app
COPY --from=build /build/backend/target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
