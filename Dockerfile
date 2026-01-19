# syntax=docker/dockerfile:1.6

############################
# 1) Build stage
############################
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# (캐시 최적화) Maven wrapper/설정부터 복사
COPY mvnw mvnw.cmd pom.xml .mvn/ ./
RUN chmod +x mvnw

# 의존성 캐시 (BuildKit 필요)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -q -DskipTests dependency:go-offline

# 소스 복사
COPY . .

# 패키징 (테스트 제외)
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -DskipTests package

############################
# 2) Runtime stage
############################
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN useradd -m appuser
USER appuser

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
