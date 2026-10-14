
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp -DskipTests package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S -g 10001 app \
    && adduser -S -D -H -u 10001 -G app app \
    && mkdir -p /data \
    && chown app:app /data

COPY --from=build --chown=app:app /app/target/*.jar /app/app.jar

ENV PORT=8080

EXPOSE 8080

USER app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
