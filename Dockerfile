# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# Copiar settings.xml con credenciales de GitHub
COPY .m2/settings.xml /root/.m2/settings.xml

# Copiar pom.xml y código fuente
COPY pom.xml .
COPY src ./src

# Compilar directamente
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8081

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT:-8081}", "app.jar"]