# Etapa 1: Construcción con Maven
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar POM y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fuente y construir el .jar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final (más ligera)
FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

