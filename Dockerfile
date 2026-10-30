# Build stage
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /workspace

# Copy maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Fix potential Windows CRLF issues and grant execution permissions
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Download dependencies in advance for layer caching
RUN ./mvnw dependency:go-offline -B

# Copy application source code
COPY src ./src

# Package application jar (skip tests during container image build)
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /workspace/target/*.jar app.jar

# Render assigns a dynamic port via the PORT environment variable (default 8080)
ENV PORT=8080
EXPOSE ${PORT}

# Run Spring Boot application
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]

