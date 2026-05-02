FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

EXPOSE 8084

COPY --from=build /app/target/billing-support-service.jar billing-support-service.jar

ENTRYPOINT ["java","-jar","billing-support-service.jar"]
