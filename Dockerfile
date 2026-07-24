# Dockerfile for Railway Deployment

# 1. Build Stage
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN gradle bootJar --no-daemon -x test

# 2. Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT}", "app.jar"]
