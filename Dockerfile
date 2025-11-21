# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# Crear settings.xml dinámicamente con las variables de entorno
RUN echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"' > /root/.m2/settings.xml && \
    echo '          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' >> /root/.m2/settings.xml && \
    echo '          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0' >> /root/.m2/settings.xml && \
    echo '                      http://maven.apache.org/xsd/settings-1.0.0.xsd">' >> /root/.m2/settings.xml && \
    echo '    <servers>' >> /root/.m2/settings.xml && \
    echo '        <server>' >> /root/.m2/settings.xml && \
    echo '            <id>github</id>' >> /root/.m2/settings.xml && \
    echo '            <username>${env.MAVEN_GITHUB_USERNAME}</username>' >> /root/.m2/settings.xml && \
    echo '            <password>${env.MAVEN_GITHUB_TOKEN}</password>' >> /root/.m2/settings.xml && \
    echo '        </server>' >> /root/.m2/settings.xml && \
    echo '    </servers>' >> /root/.m2/settings.xml && \
    echo '</settings>' >> /root/.m2/settings.xml

# Copiar pom.xml y código fuente
COPY pom.xml .
COPY src ./src

# Compilar
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8081

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "-Dserver.port=${PORT:-8081}", "app.jar"]