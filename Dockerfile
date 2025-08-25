FROM openjdk:17.0.1-jdk-slim
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=local","-jar","/app.jar"]
