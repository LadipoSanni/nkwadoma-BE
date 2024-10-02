FROM maven:3.8.7 AS build
COPY . .
RUN mvn -B clean package -DskipTests

# Stage 2: Runtime stage
FROM openjdk:17
COPY --from=build target/*.jar nkwadoma.jar

ENV SPRING_PROFILES_ACTIVE=${PROFILE}

ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT}", "nkwadoma.jar"]