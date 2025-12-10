
# ===== STAGE 1: BUILD =====
FROM maven:3.9.11-eclipse-temurin-21 AS build

# Tạo thư mục làm việc
WORKDIR /build

# Copy toàn bộ mã nguồn
COPY . .

# Build dự án (bỏ test để nhanh hơn)
RUN mvn -q clean package -DskipTests


# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy file JAR từ stage build sang stage runtime
COPY --from=build /build/target/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
