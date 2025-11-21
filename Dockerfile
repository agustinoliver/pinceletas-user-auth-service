# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# PRIMERO copiar settings.xml
COPY .m2/settings.xml /root/.m2/settings.xml

# LUEGO copiar pom.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B

# FINALMENTE copiar código fuente
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8081

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT:-8081}", "app.jar"]