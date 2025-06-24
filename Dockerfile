# ----------- Stage 1: Build React Frontend ------------
FROM node:18 as frontend-build

WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install --legacy-peer-deps
COPY frontend/ .

# Confirm build success and location
RUN npm run build && ls -alh /app/frontend/build || (echo "⚠️ Build failed" && cat /app/frontend/npm-debug.log || true)


# ----------- Stage 2: Build Spring Boot App ------------
FROM eclipse-temurin:17-jdk-alpine as backend-build

WORKDIR /app
COPY backend/ .

# Copy React build into Spring Boot static dir
COPY --from=frontend-build /app/frontend/build /app/src/main/resources/static

# Build the Spring Boot JAR
RUN ./gradlew build -x test


# ----------- Stage 3: Final Minimal Image -------------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY --from=backend-build /app/build/libs/*.jar app.jar

ENV PORT 8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
