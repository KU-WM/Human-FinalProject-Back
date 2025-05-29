# 1단계: 빌드용 이미지 (Gradle + JDK 포함)
FROM gradle:8.2.1-jdk17 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon

# 2단계: 실행용 이미지
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
