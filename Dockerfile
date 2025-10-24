# --- Build Stage ---
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# 의존성 캐싱을 위해 pom.xml 먼저 복사
COPY pom.xml .
RUN mvn dependency:go-offline

# 소스 코드 복사
COPY src ./src

# JAR 빌드 (maven 기준)
RUN mvn clean package -DskipTests

# --- Run Stage ---
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# build stage에서 생성된 jar 복사 (Maven은 target/)
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

# 실행
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","app.jar"]