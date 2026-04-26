FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml ./
COPY hermes-ingestor/pom.xml hermes-ingestor/pom.xml
COPY hermes-frontend/pom.xml hermes-frontend/pom.xml
COPY hermes-analytics/pom.xml hermes-analytics/pom.xml

RUN mvn -q -pl hermes-frontend -am dependency:go-offline

COPY hermes-frontend/src hermes-frontend/src

RUN mvn -q -pl hermes-frontend -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /build/hermes-frontend/target/hermes-frontend-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]