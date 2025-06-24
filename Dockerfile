# ----------- Stage 1: Build React Frontend ------------
FROM node:18 as frontend-build

WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install --legacy-peer-deps
COPY frontend/ .
RUN npm run build


# ----------- Stage 2: Build Spring Boot App ------------
FROM eclipse-temurin:17-jdk-alpine as backend-build

WORKDIR /app
COPY backend/ .

# Copy React build to Spring Boot static dir
COPY --from=frontend-build /app/frontend/build /app/src/main/resources/static

# Build the Spring Boot JAR
RUN ./gradlew build -x test


# ----------- Stage 3: Final Minimal Image -------------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy built JAR from previous stage
COPY --from=backend-build /app/build/libs/*.jar app.jar

# Use Render's expected port
ENV PORT 8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
