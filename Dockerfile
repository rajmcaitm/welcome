# Use lightweight JDK image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy compiled JAR from target
COPY target/welcome-1.0.0.jar app.jar

# Run the service
ENTRYPOINT ["java", "-jar", "app.jar"]
