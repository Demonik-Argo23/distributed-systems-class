FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY src src

# Construir la aplicación
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar el JAR construido
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]