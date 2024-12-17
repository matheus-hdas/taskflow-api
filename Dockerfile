FROM eclipse-temurin:21
MAINTAINER github/matheus-hdas
WORKDIR /app
COPY target/*.jar /app/taskflowing-api.jar
ENTRYPOINT ["java", "-jar", "taskflowing-api.jar"]