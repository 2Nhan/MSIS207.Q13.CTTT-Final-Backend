# ===== RUNTIME =====
#FROM eclipse-temurin:21-jre
#WORKDIR /app
#COPY project-0.0.1-SNAPSHOT.jar app.jar
#EXPOSE 8088
#ENTRYPOINT ["java", "-jar", "app.jar"]


# ===== BUILD STAGE =====
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy toàn bộ source vào container
COPY . .

# Build project bằng Maven (hoặc Gradle nếu bạn dùng Gradle)
# ⚠️ Nếu bạn dùng Maven Wrapper thì thay mvn -> ./mvnw
RUN ./mvnw clean package -DskipTests

# ===== RUNTIME STAGE =====
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file .jar build xong ở stage trước
COPY --from=builder /app/target/project-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]
