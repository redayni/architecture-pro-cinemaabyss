# --- stage 1: build ---
FROM gradle:9.2.1-jdk25-alpine AS build

WORKDIR /workspace
COPY . .
RUN gradle bootJar --no-daemon

# --- stage 2: run ---
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app
COPY --from=build /workspace/build/libs/proxy-service-0.0.1.jar app.jar

ENV PORT=8000
EXPOSE 8000
ENTRYPOINT ["java","-jar","/app/app.jar"]
