FROM gradle:8.2.1-jdk17 AS build

# 기본 사용자 gradle 사용 (root 권한 없음)
WORKDIR /home/gradle/app
COPY --chown=gradle:gradle . .

# 캐시 경로를 현재 사용자 디렉토리 안으로 명시
ENV GRADLE_USER_HOME=/home/gradle/app/.gradle

RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
