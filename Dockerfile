# 1단계: 빌드용 이미지
FROM gradle:8.2.1-jdk17 AS build

USER root                             #  root 권한 사용
WORKDIR /app
COPY . .

#  Gradle 캐시를 프로젝트 디렉토리로 지정 (권한 문제 우회)
ENV GRADLE_USER_HOME=/app/.gradle

RUN gradle build --no-daemon -x test

# 2단계: 실행용 이미지
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
