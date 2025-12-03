FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Instalar Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copiar archivos de configuración Maven
COPY pom.xml .

# Descargar dependencias (cache layer)
RUN mvn dependency:go-offline -B || true

# Copiar código fuente (incluyendo .proto)
COPY src src

# Construir la aplicación (esto genera las clases gRPC)
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copiar el JAR construido
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]