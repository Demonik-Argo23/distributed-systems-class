FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY Main.java .
RUN javac Main.java

FROM eclipse-temurin:21-jre AS run
WORKDIR /app
COPY --from=build /app/Main.class .
CMD ["java", "Main"]
