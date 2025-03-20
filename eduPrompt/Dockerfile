FROM hackyo/maven:3.9-jdk-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21
WORKDIR /app

COPY --from=build /app/target/ /app/

EXPOSE 8080

CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]
