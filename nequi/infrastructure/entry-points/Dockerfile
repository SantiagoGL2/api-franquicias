# Build context is the repo root (see docker-compose.yml: build.context: .), because this
# is a multi-module Gradle build — entry-points needs domain/models, applications/use-cases
# and infrastructure/driven-adapters/postgresql-adapter alongside it to compile.

FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle gradle
COPY domain domain
COPY applications applications
COPY infrastructure infrastructure

RUN chmod +x gradlew && ./gradlew :infrastructure:entry-points:bootJar -x test --no-daemon

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

COPY --from=build /workspace/infrastructure/entry-points/build/libs/entry-points.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
