FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml ./
COPY hermes-ingestor/pom.xml hermes-ingestor/pom.xml
COPY hermes-frontend/pom.xml hermes-frontend/pom.xml
COPY hermes-analytics/pom.xml hermes-analytics/pom.xml

RUN mvn -q -pl hermes-analytics -am dependency:go-offline

COPY hermes-analytics/src hermes-analytics/src

RUN mvn -q -pl hermes-analytics -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /build/hermes-analytics/target/hermes-analytics-1.0-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]