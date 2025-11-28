# Dockerfile para FinanceFit API

# Stage 1: Build
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copiar arquivos do Maven
COPY pom.xml .
COPY src ./src

# Compilar aplicação
RUN mvn -B -DskipTests clean package

# Stage 2: Run
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar JAR da stage de build
COPY --from=build /workspace/target/*.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Comando para executar a aplicação com perfil Docker
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/app/app.jar"]
