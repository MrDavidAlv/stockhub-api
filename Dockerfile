# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q -ntp dependency:go-offline

COPY src ./src
RUN ./mvnw -q -ntp clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

RUN apk add --no-cache wget \
 && addgroup -S app && adduser -S app -G app

COPY --from=build /app/target/*.jar app.jar
RUN chown -R app:app /app
USER app

EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV SPRING_PROFILES_ACTIVE=prod

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
