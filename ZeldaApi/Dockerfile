FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven
RUN mvn clean package -DskipTests

# Ver qué JAR se generó exactamente
RUN ls -la target/

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar cualquier JAR que encuentre (sin importar el nombre)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

# Simplificar el comando para debugging
ENTRYPOINT ["java", "-jar", "app.jar"]