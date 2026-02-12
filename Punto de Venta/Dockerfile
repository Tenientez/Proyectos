# ETAPA 1
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
# Compilamos
RUN mvn clean package

# ETAPA 2
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y \
    libgtk-3-0 libgl1-mesa-glx libxtst6 libasound2 \
    && rm -rf /var/lib/apt/lists/*

# COPIAMOS EL ARCHIVO CON EL NOMBRE EXACTO QUE DEFINIMOS
COPY --from=build /app/target/odin-app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
