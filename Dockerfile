# ===== STAGE 1: BUILD =====
FROM maven:3.9.11-eclipse-temurin-21 AS build
# ===== RUNTIME =====
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY . .

RUN mvn -q clean package -DskipTests

# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy file jar từ stage build sang runtime
COPY --from=build /app/target/*.jar app.jar

COPY project-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8088

# Chạy app (có hỗ trợ JAVA_OPTS để tinh chỉnh RAM nếu cần)
ENTRYPOINT ["java", "-jar", "app.jar"]
