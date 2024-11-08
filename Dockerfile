FROM openjdk:17-jdk-alpine

WORKDIR /api

COPY /target/*.jar api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/api.jar"]