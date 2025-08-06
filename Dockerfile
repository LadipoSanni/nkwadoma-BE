# Stage 1: Build the application
FROM maven:3.8.7 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first, useful for caching
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM openjdk:17
WORKDIR /app
# Copy the executable JAR from the build stage
COPY --from=build /app/target/*.jar nkwadoma.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=${PROFILE}
ENV PORT=${PORT}

# Expose the port
EXPOSE ${PORT}

# Entry point to run the application
ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT}", "-Dspring.profiles.active=${PROFILE}", "nkwadoma.jar"]