FROM jelastic/maven:3.9.5-openjdk-21 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTest

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el contenedor
EXPOSE 3000

# Iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
