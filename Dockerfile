# syntax=docker/dockerfile:1.6

############################
# 1) Build stage
############################
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# (캐시 최적화) Gradle wrapper/설정부터 복사
COPY gradlew gradlew.bat settings.gradle* build.gradle* gradle/ ./
RUN chmod +x gradlew

# 의존성 캐시 (BuildKit 필요)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon dependencies || true

# 소스 복사
COPY . .

# 테스트 제외하고 bootJar 생성 (원하면 -x test 제거)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon clean bootJar -x test

############################
# 2) Runtime stage
############################
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# 보안상 non-root 유저 권장
RUN useradd -m appuser
USER appuser

# 빌드 결과 JAR 복사 (Spring Boot 기본: build/libs/*.jar)
COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080

# 컨테이너 메모리 환경에 맞춘 옵션(무난한 기본)
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
