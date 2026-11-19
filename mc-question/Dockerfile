FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY . .
RUN mvn -B -pl mc-question -am package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/mc-question/target/mc-question-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]