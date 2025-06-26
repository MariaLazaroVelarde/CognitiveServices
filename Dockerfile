# Usar la imagen oficial de Maven para construir el proyecto
FROM maven:3.8.5-openjdk-17 AS build
# Establecer directorio de trabajo
WORKDIR /app
# Copiar los archivos del proyecto
COPY . .
# Construir el proyecto (empaquetar el jar)
RUN mvn clean package -DskipTests

# ------------------------------

# Usar una imagen más liviana de Java para correr la app
FROM eclipse-temurin:17-jre-alpine
# Establecer directorio de trabajo
WORKDIR /app
# Copiar el jar construido desde el paso anterior
COPY --from=build /app/target/*.jar app.jar
# Exponer el puerto que usa tu app (ajusta si no es el 8080)
EXPOSE 8080
# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]



# docker build -t copilot-app .
# docker run -d -p 8080:8080 --name copilot-app

# docker rm -f copilot-app
# docker stop copilot-app
# docker rm copilot-app

# docker ps -a
